package com.example.dotalink.integration.dota;

public record DotaExternalProfile(
        String steamId,
        String accountId,
        String profileUrl,
        String avatarUrl,
        Integer mmr
) {
}
