package com.example.dotalink.integration.opendota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenDotaProfileDto(
        String personaname,
        String avatarfull,
        String profileurl
) {
}
