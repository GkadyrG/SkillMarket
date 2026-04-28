package com.example.dotalink.feature.partypost.controller;

import com.example.dotalink.common.exception.AccessDeniedBusinessException;
import com.example.dotalink.feature.application.dto.PartyApplicationDto;
import com.example.dotalink.feature.application.model.PartyApplicationStatus;
import com.example.dotalink.feature.application.service.DuplicatePartyApplicationException;
import com.example.dotalink.feature.application.service.PartyApplicationService;
import com.example.dotalink.feature.partypost.dto.PartyPostCreateDto;
import com.example.dotalink.feature.partypost.dto.PartyPostDto;
import com.example.dotalink.feature.partypost.dto.PartyPostFilterDto;
import com.example.dotalink.feature.partypost.dto.PartyPostUpdateDto;
import com.example.dotalink.feature.profile.model.DotaRank;
import com.example.dotalink.feature.profile.model.DotaRegion;
import com.example.dotalink.feature.profile.model.DotaRolePreference;
import com.example.dotalink.feature.partypost.service.PartyPostService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PartyPostController {

    private static final Logger log = LoggerFactory.getLogger(PartyPostController.class);

    private final PartyPostService partyPostService;
    private final PartyApplicationService partyApplicationService;

    public PartyPostController(PartyPostService partyPostService,
                               PartyApplicationService partyApplicationService) {
        this.partyPostService = partyPostService;
        this.partyApplicationService = partyApplicationService;
    }

    @GetMapping({"/party", "/posts"})
    public String listPosts(@ModelAttribute("filter") PartyPostFilterDto filter,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
        model.addAttribute("postsPage", partyPostService.getPostsPage(filter, pageable));
        addPartyOptions(model);
        return "party/list";
    }

    @GetMapping("/posts/{id}")
    public String showPost(@PathVariable Long id, Authentication authentication, Model model) {
        PartyPostDto post = partyPostService.getPost(id);
        model.addAttribute("post", post);

        if (!model.containsAttribute("applicationForm")) {
            model.addAttribute("applicationForm", new PartyApplicationDto());
        }

        boolean isOwnPost = authentication != null
                && post.getAuthorUsername() != null
                && post.getAuthorUsername().equals(authentication.getName());
        boolean isPostOpen = "OPEN".equalsIgnoreCase(post.getStatus());
        boolean alreadyApplied = false;

        if (authentication != null && !isOwnPost) {
            alreadyApplied = partyApplicationService.hasAppliedToPost(id, authentication.getName());
        }

        boolean canApply = authentication != null && !isOwnPost && isPostOpen && !alreadyApplied;
        model.addAttribute("isOwnPost", isOwnPost);
        model.addAttribute("isPostOpen", isPostOpen);
        model.addAttribute("alreadyApplied", alreadyApplied);
        model.addAttribute("canApply", canApply);

        if (isOwnPost) {
            try {
                model.addAttribute("applications",
                        partyApplicationService.getApplicationsForPost(id, authentication.getName()));
                model.addAttribute("canManageApplications", true);
            } catch (Exception ex) {
                log.warn("Failed to load applications for own post: postId={}, username={}",
                        id, authentication.getName(), ex);
                model.addAttribute("applications", java.util.List.of());
                model.addAttribute("canManageApplications", false);
            }
            model.addAttribute("ownPostApplyMessage",
                    "Вы автор этого объявления. Откликаться на собственный запрос нельзя.");
        } else {
            model.addAttribute("applications", java.util.List.of());
            model.addAttribute("canManageApplications", false);
        }

        return "party/view";
    }

    @GetMapping("/posts/create")
    public String createPage(Model model) {
        if (!model.containsAttribute("postForm")) {
            model.addAttribute("postForm", new PartyPostCreateDto());
        }
        model.addAttribute("editMode", false);
        addPartyOptions(model);
        return "party/form";
    }

    @PostMapping("/posts")
    public String createPost(Authentication authentication,
                             @Valid @ModelAttribute("postForm") PartyPostCreateDto postForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", false);
            addPartyOptions(model);
            return "party/form";
        }
        Long id = partyPostService.createPost(authentication.getName(), postForm);
        redirectAttributes.addFlashAttribute("successMessage", "Party post created");
        return "redirect:/posts/" + id;
    }

    @GetMapping("/posts/{id}/edit")
    public String editPage(@PathVariable Long id, Authentication authentication, Model model) {
        if (!model.containsAttribute("postForm")) {
            model.addAttribute("postForm", partyPostService.getUpdateDto(id, authentication.getName()));
        }
        model.addAttribute("editMode", true);
        model.addAttribute("postId", id);
        addPartyOptions(model);
        return "party/form";
    }

    @PostMapping("/posts/{id}")
    public String updatePost(@PathVariable Long id,
                             Authentication authentication,
                             @Valid @ModelAttribute("postForm") PartyPostUpdateDto postForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", true);
            model.addAttribute("postId", id);
            addPartyOptions(model);
            return "party/form";
        }
        partyPostService.updatePost(id, authentication.getName(), postForm);
        redirectAttributes.addFlashAttribute("successMessage", "Party post updated");
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        partyPostService.deletePost(id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Party post deleted");
        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/apply")
    public String applyToPost(@PathVariable Long id,
                              Authentication authentication,
                              @Valid @ModelAttribute("applicationForm") PartyApplicationDto applicationForm,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            throw new AccessDeniedBusinessException("Authentication required");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Некорректные данные заявки.");
            return "redirect:/posts/" + id;
        }

        try {
            partyApplicationService.apply(id, authentication.getName(), applicationForm);
            redirectAttributes.addFlashAttribute("successMessage", "Заявка отправлена.");
        } catch (DuplicatePartyApplicationException | AccessDeniedBusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/posts/" + id;
    }

    @PostMapping("/applications/{id}/accept")
    public String acceptApplication(@PathVariable Long id,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            throw new AccessDeniedBusinessException("Authentication required");
        }
        PartyApplicationDto updated = partyApplicationService.updateStatus(
                id,
                PartyApplicationStatus.ACCEPTED,
                authentication.getName()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Application accepted");
        return "redirect:/posts/" + updated.getPostId();
    }

    @PostMapping("/applications/{id}/reject")
    public String rejectApplication(@PathVariable Long id,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            throw new AccessDeniedBusinessException("Authentication required");
        }
        PartyApplicationDto updated = partyApplicationService.updateStatus(
                id,
                PartyApplicationStatus.REJECTED,
                authentication.getName()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Application rejected");
        return "redirect:/posts/" + updated.getPostId();
    }

    private void addPartyOptions(Model model) {
        model.addAttribute("rankOptions", DotaRank.valuesList());
        model.addAttribute("regionOptions", DotaRegion.valuesList());
        model.addAttribute("roleOptions", DotaRolePreference.valuesList());
    }
}

