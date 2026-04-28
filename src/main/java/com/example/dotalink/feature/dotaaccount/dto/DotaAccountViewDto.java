package com.example.dotalink.feature.dotaaccount.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Linked Dota account view model")
public record DotaAccountViewDto(
        @Schema(description = "Internal id", example = "1")
        Long id,
        @Schema(description = "OpenDota account id", example = "123456789")
        Long accountId,
        @Schema(description = "Player persona name", example = "CaptainAdmin")
        String personaName,
        @Schema(description = "Avatar URL")
        String avatarUrl,
        @Schema(description = "Profile URL")
        String profileUrl,
        @Schema(description = "OpenDota rank tier", example = "54")
        Integer rankTier,
        @Schema(description = "Leaderboard rank", example = "120")
        Integer leaderboardRank,
        @Schema(description = "Last synchronization time")
        LocalDateTime lastSyncAt
) {
}
