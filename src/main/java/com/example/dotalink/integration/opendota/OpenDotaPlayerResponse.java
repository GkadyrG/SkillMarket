package com.example.dotalink.integration.opendota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenDotaPlayerResponse(
        OpenDotaProfileDto profile,
        Integer rank_tier,
        Integer leaderboard_rank
) {
}
