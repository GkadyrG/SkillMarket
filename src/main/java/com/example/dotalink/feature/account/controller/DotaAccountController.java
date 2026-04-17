package com.example.dotalink.feature.dotaaccount.controller;

import com.example.dotalink.feature.dotaaccount.dto.DotaAccountForm;
import com.example.dotalink.feature.dotaaccount.service.DotaAccountService;
import com.example.dotalink.integration.dota.SteamApiProperties;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DotaAccountController {

    private final DotaAccountService dotaAccountService;
    private final SteamApiProperties steamApiProperties;

    public DotaAccountController(DotaAccountService dotaAccountService,
                                 SteamApiProperties steamApiProperties) {
        this.dotaAccountService = dotaAccountService;
        this.steamApiProperties = steamApiProperties;
    }

    @GetMapping("/account/dota")
    public String dotaAccountPage(Authentication authentication, Model model) {
        var account = dotaAccountService.getForUser(authentication.getName());
        model.addAttribute("account", account.orElse(null));
        model.addAttribute("steamIntegrationEnabled", steamApiProperties.isEnabled());

        if (!model.containsAttribute("dotaAccountForm")) {
            model.addAttribute("dotaAccountForm", account.map(dotaAccountService::toForm).orElse(new DotaAccountForm()));
        }
        return "account/dota";
    }

    @PostMapping("/account/dota")
    public String saveDotaAccount(
            Authentication authentication,
            @Valid @ModelAttribute("dotaAccountForm") DotaAccountForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("account", dotaAccountService.getForUser(authentication.getName()).orElse(null));
            return "account/dota";
        }

        dotaAccountService.upsertForUser(authentication.getName(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Dota account saved successfully");
        return "redirect:/account/dota";
    }

    @PostMapping("/account/dota/delete")
    public String deleteDotaAccount(Authentication authentication, RedirectAttributes redirectAttributes) {
        dotaAccountService.deleteForUser(authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Dota account deleted");
        return "redirect:/account/dota";
    }
}
