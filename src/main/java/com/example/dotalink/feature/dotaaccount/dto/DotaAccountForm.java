package com.example.dotalink.feature.dotaaccount.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Dota account link form")
public class DotaAccountForm {

    @NotNull(message = "Dota account ID is required")
    @Positive(message = "Dota account ID must be a positive number")
    @Schema(description = "OpenDota account id", example = "123456789")
    private Long accountId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
