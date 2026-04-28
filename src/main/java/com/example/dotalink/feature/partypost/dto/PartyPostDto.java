package com.example.dotalink.feature.partypost.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Party post response model")
public class PartyPostDto {

    @Schema(description = "Post id", example = "1")
    private Long id;
    @Schema(description = "Post title", example = "Need pos4 for ranked")
    private String title;
    @Schema(description = "Post description")
    private String description;
    @Schema(description = "Required rank", example = "Legend")
    private String requiredRank;
    @Schema(description = "Needed role", example = "Soft Support")
    private String roleNeeded;
    @Schema(description = "Region", example = "EU West")
    private String region;
    @Schema(description = "Current status", example = "OPEN")
    private String status;
    @Schema(description = "Author username", example = "demo")
    private String authorUsername;
    @Schema(description = "Creation time")
    private LocalDateTime createdAt;
    @Schema(description = "Applications count", example = "2")
    private long applicationsCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getApplicationsCount() {
        return applicationsCount;
    }

    public void setApplicationsCount(long applicationsCount) {
        this.applicationsCount = applicationsCount;
    }
}
