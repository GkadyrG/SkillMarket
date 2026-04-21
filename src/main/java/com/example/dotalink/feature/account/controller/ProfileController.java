package com.example.dotalink.feature.profile.controller;

import com.example.dotalink.common.exception.AccessDeniedBusinessException;
import com.example.dotalink.feature.profile.dto.UserProfileDto;
import com.example.dotalink.feature.profile.model.DotaRank;
import com.example.dotalink.feature.profile.service.ProfileService;
import com.example.dotalink.feature.profile.service.UserStatsService;
import com.example.dotalink.feature.review.dto.ReviewCreateDto;
import com.example.dotalink.feature.review.service.ReviewService;
import jakarta.validation.Valid;
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
public class ProfileController {

    private final ProfileService profileService;
    private final ReviewService reviewService;
    private final UserStatsService userStatsService;

    public ProfileController(ProfileService profileService,
                             ReviewService reviewService,
                             UserStatsService userStatsService) {
        this.profileService = profileService;
        this.reviewService = reviewService;
        this.userStatsService = userStatsService;
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
    public String publicProfile(@PathVariable String username, Authentication authentication, Model model) {
        model.addAttribute("profile", profileService.getPublicProfile(username));
        model.addAttribute("reviews", reviewService.getReviewsForUser(username));
        model.addAttribute("averageRating", reviewService.getAverageRatingForUser(username));
        model.addAttribute("canReview", authentication != null && !authentication.getName().equals(username));
        if (!model.containsAttribute("reviewForm")) {
            model.addAttribute("reviewForm", new ReviewCreateDto());
        }
        return "players/public-profile";
    }

    @PostMapping("/profiles/{username}/reviews")
    public String createReview(@PathVariable String username,
                               Authentication authentication,
                               @Valid @ModelAttribute("reviewForm") ReviewCreateDto reviewForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            throw new AccessDeniedBusinessException("Authentication required");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Review data is invalid");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reviewForm", bindingResult);
            redirectAttributes.addFlashAttribute("reviewForm", reviewForm);
            return "redirect:/profiles/" + username;
        }

        try {
            reviewService.createReview(authentication.getName(), username, reviewForm);
            redirectAttributes.addFlashAttribute("successMessage", "Review created");
        } catch (AccessDeniedBusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/profiles/" + username;
    }

    @GetMapping("/profiles/{username}/stats")
    public String userStats(@PathVariable String username, Model model) {
        model.addAttribute("stats", userStatsService.getStatsByUsername(username));
        return "profile/stats";
    }

    @GetMapping("/profiles/stats/review-leaders")
    public String reviewLeaders(@RequestParam(defaultValue = "1") long minReviews, Model model) {
        model.addAttribute("minReviews", Math.max(minReviews, 1));
        model.addAttribute("leaders", userStatsService.getUsersAbovePlatformAverage(Math.max(minReviews, 1)));
        return "profile/review-analytics";
    }
}
