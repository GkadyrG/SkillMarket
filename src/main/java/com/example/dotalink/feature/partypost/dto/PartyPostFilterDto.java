package com.example.dotalink.feature.partypost.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Party posts search filter")
public class PartyPostFilterDto {

    @Schema(description = "Rank filter", example = "Legend")
    private String rank;
    @Schema(description = "Role filter", example = "Soft Support")
    private String role;
    @Schema(description = "Region filter", example = "EU West")
    private String region;
    @Schema(description = "Search implementation mode", allowableValues = {"criteria", "jpql"}, example = "criteria")
    private String mode = "criteria";

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
