package com.example.dotalink.feature.partypost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Party post update payload")
public class PartyPostUpdateDto extends PartyPostCreateDto {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "OPEN|CLOSED", message = "Status must be OPEN or CLOSED")
    @Size(max = 20, message = "Status is too long")
    @Schema(description = "Post status", allowableValues = {"OPEN", "CLOSED"}, example = "OPEN")
    private String status = "OPEN";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
