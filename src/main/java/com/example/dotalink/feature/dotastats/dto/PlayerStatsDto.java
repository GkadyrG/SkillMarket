package com.example.dotalink.feature.dotastats.dto;

import java.util.List;

public record PlayerStatsDto(
        String nickname,
        String avatar,
        String rank,
        Integer leaderboardRank,
        double winrate,
        double avgKda,
        double avgKills,
        double avgDeaths,
        double avgAssists,
        List<HeroStatsDto> topHeroes,
        List<MatchDto> recentMatches
) {
}
