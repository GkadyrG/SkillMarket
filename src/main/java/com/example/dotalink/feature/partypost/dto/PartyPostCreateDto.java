package com.example.dotalink.feature.partypost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PartyPostCreateDto {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title is too long")
    private String title;

    @Size(max = 2000, message = "Description is too long")
    private String description;

    @Pattern(
            regexp = "^$|^(Herald|Guardian|Crusader|Archon|Legend|Ancient|Divine|Immortal)$",
            message = "Rank must be a valid Dota rank"
    )
    private String requiredRank;

    @Pattern(
            regexp = "^$|^(Carry|Mid|Offlane|Soft Support|Hard Support)$",
            message = "Role must be a valid Dota role"
    )
    private String roleNeeded;

    @Pattern(
            regexp = "^$|^(EU West|EU East|CIS|North America|South America|Southeast Asia|China)$",
            message = "Region must be a valid region"
    )
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
