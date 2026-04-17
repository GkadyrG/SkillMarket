package com.example.dotalink.feature.auth.service;

import com.example.dotalink.feature.auth.dto.RegistrationForm;

@Deprecated
public class RegistrationService {

    private final AuthService authService;

    public RegistrationService(AuthService authService) {
        this.authService = authService;
    }

    public Long register(RegistrationForm form) {
        return authService.register(form);
    }

    public boolean isUsernameAvailable(String username) {
        return false;
    }

    public boolean isEmailAvailable(String email) {
        return false;
    }
}
