package com.example.dotalink.feature.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Players search filter")
public class PlayerSearchFilter {

    @Schema(description = "Nickname substring", example = "capt")
    private String nickname;
    @Schema(description = "Exact rank filter", example = "Archon")
    private String rank;
    @Schema(description = "Exact region filter", example = "EU West")
    private String region;
    @Schema(description = "Search implementation mode", allowableValues = {"criteria", "jpql"}, example = "criteria")
    private String mode = "criteria";

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
