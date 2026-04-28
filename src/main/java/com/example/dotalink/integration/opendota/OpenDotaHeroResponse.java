package com.example.dotalink.integration.opendota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenDotaHeroResponse(
        Integer id,
        String name,
        @JsonProperty("localized_name") String localizedName,
        String img
) {
}
