package com.example.dotalink.feature.account.dto;

import java.time.LocalDateTime;

public record DotaAccountResponse(
        Long id,
        String steamId,
        String accountId,
        String avatarUrl,
        String profileUrl,
        Integer mmr,
        LocalDateTime lastSyncAt
) {
}
