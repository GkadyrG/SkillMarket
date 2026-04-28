package com.example.dotalink.feature.auth.controller;

import com.example.dotalink.common.exception.DuplicateEmailException;
import com.example.dotalink.common.exception.DuplicateUsernameException;
import com.example.dotalink.feature.auth.dto.LoginRequest;
import com.example.dotalink.feature.auth.dto.RegisterRequest;
import com.example.dotalink.feature.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Tag(name = "Auth", description = "Authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(AuthService authService,
                          SecurityContextRepository securityContextRepository) {
        this.authService = authService;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping("/login")
    @Operation(summary = "Open login page", description = "Returns the HTML login form")
    public String login(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "auth/login";
    }

    @GetMapping("/register")
    @Operation(summary = "Open registration page", description = "Returns the HTML registration form")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and an initial profile")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(form);
            return "redirect:/login?registered";
        } catch (DuplicateUsernameException ex) {
            bindingResult.rejectValue("username", "duplicate.username", ex.getMessage());
            return "auth/register";
        } catch (DuplicateEmailException ex) {
            bindingResult.rejectValue("email", "duplicate.email", ex.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user and stores the security context in session")
    public String login(
            @Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            Authentication authentication = authService.login(loginRequest);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            return "redirect:/profile/me";
        } catch (BadCredentialsException ex) {
            bindingResult.reject("login.invalid", "Invalid username or password.");
            return "auth/login";
        }
    }
}
