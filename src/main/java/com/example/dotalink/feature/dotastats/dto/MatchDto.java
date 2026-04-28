package com.example.dotalink.feature.dotastats.dto;

import java.time.Instant;

public record MatchDto(
        String heroName,
        int kills,
        int deaths,
        int assists,
        double kda,
        double duration,
        MatchResult result,
        Instant startTime
) {
}
