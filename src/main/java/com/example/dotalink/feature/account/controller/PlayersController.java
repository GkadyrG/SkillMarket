package com.example.dotalink.feature.account.controller;

import com.example.dotalink.feature.account.dto.PlayerSearchFilter;
import com.example.dotalink.feature.account.model.DotaRank;
import com.example.dotalink.feature.account.service.PlayerSearchService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PlayersController {

    private final PlayerSearchService playerSearchService;

    public PlayersController(PlayerSearchService playerSearchService) {
        this.playerSearchService = playerSearchService;
    }

    @GetMapping("/players")
    public String players(
            @ModelAttribute("filter") PlayerSearchFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
        model.addAttribute("playersPage", playerSearchService.search(filter, pageable));
        model.addAttribute("rankOptions", DotaRank.valuesList());
        return "players/list";
    }
}
