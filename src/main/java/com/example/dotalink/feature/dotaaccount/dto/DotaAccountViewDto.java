package com.example.dotalink.feature.dotaaccount.dto;

import java.time.LocalDateTime;

public record DotaAccountViewDto(
        Long id,
        String steamId,
        String accountId,
        String avatarUrl,
        String profileUrl,
        Integer mmr,
        LocalDateTime lastSyncAt
) {
}
