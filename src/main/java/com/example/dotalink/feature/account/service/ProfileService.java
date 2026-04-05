package com.example.dotalink.feature.account.service;

import com.example.dotalink.common.exception.ProfileNotFoundException;
import com.example.dotalink.common.exception.UserNotFoundException;
import com.example.dotalink.feature.account.dto.ProfileEditForm;
import com.example.dotalink.feature.account.model.Hero;
import com.example.dotalink.feature.account.model.User;
import com.example.dotalink.feature.account.model.UserProfile;
import com.example.dotalink.feature.account.model.DotaRank;
import com.example.dotalink.feature.account.repository.HeroRepository;
import com.example.dotalink.feature.account.repository.UserProfileRepository;
import com.example.dotalink.feature.account.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final HeroRepository heroRepository;

    public ProfileService(UserRepository userRepository,
                          UserProfileRepository userProfileRepository,
                          HeroRepository heroRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.heroRepository = heroRepository;
    }

    @Transactional(readOnly = true)
    public UserProfile getMyProfile(String username) {
        UserProfile profile = userProfileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for user: " + username));
        profile.getFavoriteHeroes().size();
        return profile;
    }

    @Transactional(readOnly = true)
    public UserProfile getPublicProfile(String username) {
        UserProfile profile = userProfileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for username: " + username));
        if (profile.getUser() != null) {
            profile.getUser().getUsername();
        }
        return profile;
    }

    @Transactional(readOnly = true)
    public ProfileEditForm getEditForm(String username) {
        UserProfile profile = getMyProfile(username);

        ProfileEditForm form = new ProfileEditForm();
        form.setNickname(profile.getNickname());
        form.setRank(profile.getRank());
        form.setRegion(profile.getRegion());
        form.setPlayTime(profile.getPlayTime());
        form.setAbout(profile.getAbout());
        form.setPreferredRolesText(profile.getPreferredRolesText());
        form.setFavoriteHeroIds(profile.getFavoriteHeroes().stream().map(Hero::getId).toList());
        return form;
    }

    @Transactional(readOnly = true)
    public List<Hero> getAllHeroes() {
        return heroRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public void updateMyProfile(String username, ProfileEditForm form) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for user id: " + user.getId()));

        profile.setNickname(form.getNickname().trim());
        profile.setRank(DotaRank.normalizeOrNull(form.getRank()));
        profile.setRegion(clean(form.getRegion()));
        profile.setPlayTime(clean(form.getPlayTime()));
        profile.setAbout(clean(form.getAbout()));
        profile.setPreferredRolesText(clean(form.getPreferredRolesText()));

        Set<Hero> heroes = new HashSet<>();
        if (form.getFavoriteHeroIds() != null && !form.getFavoriteHeroIds().isEmpty()) {
            heroes.addAll(heroRepository.findAllById(form.getFavoriteHeroIds()));
        }
        profile.setFavoriteHeroes(heroes);

        userProfileRepository.save(profile);
        log.info("Profile updated: userId={}, username={}", user.getId(), user.getUsername());
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
