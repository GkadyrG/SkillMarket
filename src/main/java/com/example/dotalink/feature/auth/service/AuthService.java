package com.example.dotalink.feature.auth.service;

import com.example.dotalink.common.exception.DuplicateEmailException;
import com.example.dotalink.common.exception.DuplicateUsernameException;
import com.example.dotalink.feature.auth.dto.LoginRequest;
import com.example.dotalink.feature.auth.dto.RegisterRequest;
import com.example.dotalink.feature.profile.model.UserProfile;
import com.example.dotalink.feature.user.model.Role;
import com.example.dotalink.feature.user.model.User;
import com.example.dotalink.feature.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public Long register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("Username is already in use");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email is already in use");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        UserProfile profile = new UserProfile();
        profile.setNickname(username);
        profile.setUser(user);
        user.setProfile(profile);

        User savedUser = userRepository.save(user);
        log.info("Registered user: id={}, username={}", savedUser.getId(), savedUser.getUsername());
        return savedUser.getId();
    }

    public Authentication login(LoginRequest request) {
        return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.getUsername().trim(),
                        request.getPassword()
                )
        );
    }
}
