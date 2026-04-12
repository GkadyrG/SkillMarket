package com.example.dotalink.feature.profile.controller;

import com.example.dotalink.feature.profile.dto.UserProfileDto;
import com.example.dotalink.feature.profile.model.DotaRank;
import com.example.dotalink.feature.profile.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile/me")
    public String myProfile(Authentication authentication, Model model) {
        model.addAttribute("profile", profileService.getMyProfile(authentication.getName()));
        return "profile/me";
    }

    @GetMapping("/profile/edit")
    public String editProfilePage(Authentication authentication, Model model) {
        if (!model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", profileService.getEditDto(authentication.getName()));
        }
        model.addAttribute("rankOptions", DotaRank.valuesList());
        model.addAttribute("heroes", profileService.getAllHeroes());
        return "profile/edit";
    }

    @PostMapping("/profile/edit")
    public String editProfile(
            Authentication authentication,
            @Valid @ModelAttribute("profileForm") UserProfileDto profileForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("rankOptions", DotaRank.valuesList());
            model.addAttribute("heroes", profileService.getAllHeroes());
            return "profile/edit";
        }

        profileService.updateMyProfile(authentication.getName(), profileForm);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        return "redirect:/profile/me";
    }

    @GetMapping("/profiles/{username}")
    public String publicProfile(@PathVariable String username, Model model) {
        model.addAttribute("profile", profileService.getPublicProfile(username));
        return "players/public-profile";
    }
}
