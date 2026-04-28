package com.example.dotalink.feature.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Party application request and response model")
public class PartyApplicationDto {

    @Size(max = 1000, message = "Application message is too long")
    @Schema(description = "Application message", example = "Can join after 19:00")
    private String message;

    @Schema(description = "Application id", example = "5")
    private Long id;
    @Schema(description = "Related post id", example = "1")
    private Long postId;
    @Schema(description = "Applicant username", example = "demo")
    private String applicantUsername;
    @Schema(description = "Application status", example = "NEW")
    private String status;
    @Schema(description = "Creation time")
    private LocalDateTime createdAt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getApplicantUsername() {
        return applicantUsername;
    }

    public void setApplicantUsername(String applicantUsername) {
        this.applicantUsername = applicantUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
