package com.example.dotalink.integration.opendota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenDotaPlayerHeroStatsResponse(
        @JsonProperty("hero_id") Integer heroId,
        Long games,
        Long win
) {
}
