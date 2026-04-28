package com.example.dotalink.feature.dotaaccount.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DotaAccountForm {

    @NotNull(message = "Dota account ID is required")
    @Positive(message = "Dota account ID must be a positive number")
    private Long accountId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
