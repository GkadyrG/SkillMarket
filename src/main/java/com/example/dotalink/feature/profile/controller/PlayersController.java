package com.example.dotalink.feature.profile.controller;

import com.example.dotalink.feature.profile.dto.PlayerSearchFilter;
import com.example.dotalink.feature.profile.model.DotaRank;
import com.example.dotalink.feature.profile.service.PlayerDirectoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Tag(name = "Players", description = "Players directory and search")
public class PlayersController {

    private final PlayerDirectoryService playerSearchService;

    public PlayersController(PlayerDirectoryService playerSearchService) {
        this.playerSearchService = playerSearchService;
    }

    @GetMapping("/players")
    @Operation(summary = "Search players", description = "Returns players list page with filters and pagination")
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
