package com.example.dotalink.feature.auth.controller;

import com.example.dotalink.feature.auth.service.RegistrationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final RegistrationService registrationService;

    public AuthApiController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/check-username")
    public Map<String, Object> checkUsername(@RequestParam("value") String value) {
        return Map.of(
                "field", "username",
                "value", value,
                "available", registrationService.isUsernameAvailable(value)
        );
    }

    @GetMapping("/check-email")
    public Map<String, Object> checkEmail(@RequestParam("value") String value) {
        return Map.of(
                "field", "email",
                "value", value,
                "available", registrationService.isEmailAvailable(value)
        );
    }
}
