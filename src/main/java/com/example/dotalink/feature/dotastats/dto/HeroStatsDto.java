package com.example.dotalink.feature.dotastats.dto;

public record HeroStatsDto(
        String heroName,
        long games,
        long wins,
        double winrate
) {
}
