package com.example.dotalink.feature.account.service;

import com.example.dotalink.feature.account.dto.DotaAccountForm;
import com.example.dotalink.feature.account.model.Role;
import com.example.dotalink.feature.account.model.User;
import com.example.dotalink.feature.account.model.UserProfile;
import com.example.dotalink.feature.account.repository.DotaAccountRepository;
import com.example.dotalink.feature.account.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DotaAccountServiceTest {

    @Autowired
    private DotaAccountService dotaAccountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DotaAccountRepository dotaAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUser() {
        if (userRepository.findByUsername("dota_user").isPresent()) {
            return;
        }

        User user = new User();
        user.setUsername("dota_user");
        user.setEmail("dota_user@example.com");
        user.setPasswordHash(passwordEncoder.encode("secret123"));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        UserProfile profile = new UserProfile();
        profile.setNickname("DotaUser");
        profile.setUser(user);
        user.setProfile(profile);

        userRepository.save(user);
    }

    @Test
    void upsertForUser_createsAndDeletesAccount() {
        DotaAccountForm form = new DotaAccountForm();
        form.setSteamId("76561198000000000");
        form.setAccountId("12345");
        form.setProfileUrl("https://steamcommunity.com/id/dota_user");
        form.setAvatarUrl("https://img.example/avatar.png");

        var saved = dotaAccountService.upsertForUser("dota_user", form);
        assertThat(saved.getId()).isNotNull();

        assertThat(dotaAccountRepository.findByUserUsername("dota_user")).isPresent();

        dotaAccountService.deleteForUser("dota_user");
        assertThat(dotaAccountRepository.findByUserUsername("dota_user")).isEmpty();
    }
}
