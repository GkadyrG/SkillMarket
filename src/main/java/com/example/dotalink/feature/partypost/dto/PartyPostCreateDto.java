package com.example.dotalink.feature.partypost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PartyPostCreateDto {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title is too long")
    private String title;

    @Size(max = 2000, message = "Description is too long")
    private String description;

    @Size(max = 40, message = "Rank is too long")
    private String requiredRank;

    @Size(max = 40, message = "Role is too long")
    private String roleNeeded;

    @Size(max = 40, message = "Region is too long")
    private String region;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredRank() {
        return requiredRank;
    }

    public void setRequiredRank(String requiredRank) {
        this.requiredRank = requiredRank;
    }

    public String getRoleNeeded() {
        return roleNeeded;
    }

    public void setRoleNeeded(String roleNeeded) {
        this.roleNeeded = roleNeeded;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
