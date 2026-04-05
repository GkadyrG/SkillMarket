package com.example.dotalink.feature.account.controller;

import com.example.dotalink.feature.account.dto.DotaAccountForm;
import com.example.dotalink.feature.account.dto.DotaAccountResponse;
import com.example.dotalink.feature.account.service.DotaAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/account/dota")
public class DotaAccountApiController {

    private final DotaAccountService dotaAccountService;

    public DotaAccountApiController(DotaAccountService dotaAccountService) {
        this.dotaAccountService = dotaAccountService;
    }

    @GetMapping
    public DotaAccountResponse get(Authentication authentication) {
        return dotaAccountService.toResponse(dotaAccountService.getRequiredForUser(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<DotaAccountResponse> create(Authentication authentication, @Valid @RequestBody DotaAccountForm form) {
        var saved = dotaAccountService.upsertForUser(authentication.getName(), form);
        return ResponseEntity.status(201).body(dotaAccountService.toResponse(saved));
    }

    @PutMapping
    public DotaAccountResponse update(Authentication authentication, @Valid @RequestBody DotaAccountForm form) {
        var saved = dotaAccountService.upsertForUser(authentication.getName(), form);
        return dotaAccountService.toResponse(saved);
    }

    @DeleteMapping
    public Map<String, Object> delete(Authentication authentication) {
        dotaAccountService.deleteForUser(authentication.getName());
        return Map.of("deleted", true);
    }
}
