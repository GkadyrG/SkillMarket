package com.example.dotalink.integration.opendota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenDotaRecentMatchResponse(
        @JsonProperty("match_id") Long matchId,
        @JsonProperty("hero_id") Integer heroId,
        Integer kills,
        Integer deaths,
        Integer assists,
        Integer duration,
        @JsonProperty("start_time") Long startTime,
        @JsonProperty("player_slot") Integer playerSlot,
        @JsonProperty("radiant_win") Boolean radiantWin
) {
}
