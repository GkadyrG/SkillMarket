package com.example.dotalink.feature.dotaaccount.dto;

import java.time.LocalDateTime;

public record DotaAccountViewDto(
        Long id,
        Long accountId,
        String personaName,
        String avatarUrl,
        String profileUrl,
        Integer rankTier,
        Integer leaderboardRank,
        LocalDateTime lastSyncAt
) {
}
