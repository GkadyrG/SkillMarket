package com.example.dotalink.feature.auth.service;

import com.example.dotalink.common.exception.DuplicateEmailException;
import com.example.dotalink.common.exception.DuplicateUsernameException;
import com.example.dotalink.feature.account.model.Role;
import com.example.dotalink.feature.account.model.User;
import com.example.dotalink.feature.account.model.UserProfile;
import com.example.dotalink.feature.account.repository.UserRepository;
import com.example.dotalink.feature.auth.dto.RegistrationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Long register(RegistrationForm form) {
        String username = form.getUsername().trim();
        String email = form.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("Username is already in use");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email is already in use");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        UserProfile profile = new UserProfile();
        profile.setNickname(username);
        profile.setUser(user);
        user.setProfile(profile);

        User saved = userRepository.save(user);
        log.info("User created successfully: id={}, username={}", saved.getId(), saved.getUsername());
        return saved.getId();
    }

    public boolean isUsernameAvailable(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return !userRepository.existsByUsername(username.trim());
    }

    public boolean isEmailAvailable(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return !userRepository.existsByEmail(email.trim().toLowerCase());
    }
}
