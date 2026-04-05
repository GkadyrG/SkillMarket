package com.example.dotalink.feature.account.service;

import com.example.dotalink.feature.account.dto.ProfileEditForm;
import com.example.dotalink.feature.account.model.Hero;
import com.example.dotalink.feature.account.model.Role;
import com.example.dotalink.feature.account.model.User;
import com.example.dotalink.feature.account.model.UserProfile;
import com.example.dotalink.feature.account.repository.HeroRepository;
import com.example.dotalink.feature.account.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProfileServiceTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HeroRepository heroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUser() {
        if (userRepository.findByUsername("profile_user").isPresent()) {
            return;
        }

        User user = new User();
        user.setUsername("profile_user");
        user.setEmail("profile_user@example.com");
        user.setPasswordHash(passwordEncoder.encode("secret123"));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        UserProfile profile = new UserProfile();
        profile.setNickname("ProfileUser");
        profile.setUser(user);
        user.setProfile(profile);

        userRepository.save(user);
    }

    @Test
    void updateMyProfile_updatesMainFieldsAndHeroes() {
        Hero hero = new Hero();
        hero.setDotaHeroId(999);
        hero.setName("Test Hero");
        hero = heroRepository.save(hero);

        ProfileEditForm form = new ProfileEditForm();
        form.setNickname("UpdatedNick");
        form.setRank("Legend");
        form.setRegion("EU");
        form.setPlayTime("Evening");
        form.setAbout("About me");
        form.setPreferredRolesText("mid,support");
        form.setFavoriteHeroIds(List.of(hero.getId()));

        profileService.updateMyProfile("profile_user", form);

        UserProfile updated = profileService.getMyProfile("profile_user");
        assertThat(updated.getNickname()).isEqualTo("UpdatedNick");
        assertThat(updated.getFavoriteHeroes()).extracting(Hero::getName).contains("Test Hero");
    }
}
