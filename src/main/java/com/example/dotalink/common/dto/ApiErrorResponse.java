package com.example.dotalink.common.dto;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        String error,
        String message,
        OffsetDateTime timestamp
) {
    public static ApiErrorResponse of(String error, String message) {
        return new ApiErrorResponse(error, message, OffsetDateTime.now());
    }
}
