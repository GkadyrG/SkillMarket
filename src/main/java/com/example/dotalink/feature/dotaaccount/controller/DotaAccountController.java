package com.example.dotalink.feature.dotaaccount.controller;

import com.example.dotalink.common.exception.DotaAccountNotFoundException;
import com.example.dotalink.common.exception.ExternalApiException;
import com.example.dotalink.feature.dotaaccount.dto.DotaAccountForm;
import com.example.dotalink.feature.dotaaccount.model.DotaAccount;
import com.example.dotalink.feature.dotaaccount.service.DotaAccountService;
import com.example.dotalink.feature.dotastats.service.DotaStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Tag(name = "Dota Account", description = "Dota account link and management")
public class DotaAccountController {

    private static final Logger log = LoggerFactory.getLogger(DotaAccountController.class);

    private final DotaAccountService dotaAccountService;
    private final DotaStatsService dotaStatsService;

    public DotaAccountController(DotaAccountService dotaAccountService,
                                 DotaStatsService dotaStatsService) {
        this.dotaAccountService = dotaAccountService;
        this.dotaStatsService = dotaStatsService;
    }

    @GetMapping("/profile/dota/link")
    @Operation(summary = "Open Dota account page")
    public String dotaAccountPage(Authentication authentication, Model model) {
        var account = dotaAccountService.getForUser(authentication.getName());
        model.addAttribute("account", account.orElse(null));
        model.addAttribute("accountRankDisplay", account
                .map(dotaAccount -> dotaStatsService.formatRankTier(dotaAccount.getRankTier()))
                .orElse("-"));
        populateStatsSection(authentication.getName(), account.orElse(null), model);

        if (!model.containsAttribute("dotaAccountForm")) {
            DotaAccountForm form = new DotaAccountForm();
            account.ifPresent(dotaAccount -> form.setAccountId(dotaAccount.getAccountId()));
            model.addAttribute("dotaAccountForm", form);
        }
        return "profile/dota-link";
    }

    @PostMapping("/profile/dota/link")
    @Operation(summary = "Create or update linked Dota account")
    public String saveDotaAccount(
            Authentication authentication,
            @Valid @ModelAttribute("dotaAccountForm") DotaAccountForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("account", dotaAccountService.getForUser(authentication.getName()).orElse(null));
            model.addAttribute("accountRankDisplay", dotaAccountService.getForUser(authentication.getName())
                    .map(dotaAccount -> dotaStatsService.formatRankTier(dotaAccount.getRankTier()))
                    .orElse("-"));
            populateStatsSection(authentication.getName(),
                    dotaAccountService.getForUser(authentication.getName()).orElse(null),
                    model);
            return "profile/dota-link";
        }

        try {
            dotaAccountService.linkDotaAccount(authentication.getName(), form.getAccountId());
        } catch (DotaAccountNotFoundException | ExternalApiException | IllegalArgumentException ex) {
            model.addAttribute("account", dotaAccountService.getForUser(authentication.getName()).orElse(null));
            model.addAttribute("accountRankDisplay", dotaAccountService.getForUser(authentication.getName())
                    .map(dotaAccount -> dotaStatsService.formatRankTier(dotaAccount.getRankTier()))
                    .orElse("-"));
            populateStatsSection(authentication.getName(),
                    dotaAccountService.getForUser(authentication.getName()).orElse(null),
                    model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "profile/dota-link";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Dota account linked successfully");
        return "redirect:/profile/dota/link";
    }

    @PostMapping("/profile/dota/unlink")
    @Operation(summary = "Delete linked Dota account")
    public String deleteDotaAccount(Authentication authentication, RedirectAttributes redirectAttributes) {
        dotaAccountService.deleteForUser(authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Dota account unlinked");
        return "redirect:/profile/me";
    }

    private void populateStatsSection(String username, DotaAccount account, Model model) {
        if (account == null) {
            model.addAttribute("playerStats", null);
            return;
        }

        try {
            dotaStatsService.syncHeroCatalogIfNeeded();
            dotaStatsService.syncPlayerStatsIfMissing(account.getAccountId());
        } catch (Exception ex) {
            log.warn("Failed to sync Dota stats for username={}, accountId={}. Page will stay available.",
                    username, account.getAccountId(), ex);
            model.addAttribute("statsWarningMessage",
                    "OpenDota stats are unavailable for this account right now. You can still change the Dota account ID.");
        }

        addStatsIfAvailable(username, model);
    }

    private void addStatsIfAvailable(String username, Model model) {
        try {
            model.addAttribute("playerStats", dotaStatsService.getPlayerStatsByUsername(username));
        } catch (Exception ex) {
            log.warn("Failed to load Dota stats for username={}. Stats block will be hidden.", username, ex);
            model.addAttribute("playerStats", null);
        }
    }
}
