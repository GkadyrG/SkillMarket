package com.example.dotalink.feature.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Login form payload")
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username is too long")
    @Schema(description = "Username", example = "demo")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(max = 64, message = "Password is too long")
    @Schema(description = "User password", example = "demo123")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
