package com.example.dotalink.feature.profile.service;

import com.example.dotalink.common.exception.ProfileNotFoundException;
import com.example.dotalink.common.exception.UserNotFoundException;
import com.example.dotalink.feature.hero.model.Hero;
import com.example.dotalink.feature.hero.repository.HeroRepository;
import com.example.dotalink.feature.profile.dto.UserProfileDto;
import com.example.dotalink.feature.profile.model.DotaRank;
import com.example.dotalink.feature.profile.model.DotaRegion;
import com.example.dotalink.feature.profile.model.DotaRolePreference;
import com.example.dotalink.feature.profile.model.UserProfile;
import com.example.dotalink.feature.profile.repository.UserProfileRepository;
import com.example.dotalink.feature.user.model.User;
import com.example.dotalink.feature.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

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
    public UserProfileDto getMyProfile(String username) {
        return toDto(loadProfileByUsername(username));
    }

    @Transactional(readOnly = true)
    public UserProfileDto getPublicProfile(String username) {
        return toDto(loadProfileByUsername(username));
    }

    @Transactional(readOnly = true)
    public UserProfileDto getEditDto(String username) {
        return toDto(loadProfileByUsername(username));
    }

    @Transactional(readOnly = true)
    public List<Hero> getAllHeroes() {
        return heroRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public void updateMyProfile(String username, UserProfileDto form) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for user id: " + user.getId()));

        profile.setNickname(form.getNickname().trim());
        profile.setRank(DotaRank.normalizeOrNull(form.getRank()));
        profile.setRegion(DotaRegion.normalizeOrNull(form.getRegion()));
        profile.setPlayTime(null);
        profile.setAbout(clean(form.getAbout()));
        profile.setPreferredRolesText(toPreferredRolesText(form.getPreferredRoles()));

        Set<Hero> heroes = new HashSet<>();
        if (form.getFavoriteHeroIds() != null && !form.getFavoriteHeroIds().isEmpty()) {
            heroes.addAll(heroRepository.findAllById(form.getFavoriteHeroIds()));
        }
        profile.setFavoriteHeroes(heroes);

        userProfileRepository.save(profile);
        log.info("Profile updated: userId={}, username={}", user.getId(), user.getUsername());
    }

    private UserProfile loadProfileByUsername(String username) {
        UserProfile profile = userProfileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for user: " + username));
        profile.getFavoriteHeroes().size();
        if (profile.getUser() != null) {
            profile.getUser().getUsername();
        }
        return profile;
    }

    private UserProfileDto toDto(UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setNickname(profile.getNickname());
        dto.setRank(profile.getRank());
        dto.setRegion(profile.getRegion());
        dto.setAbout(profile.getAbout());
        dto.setPreferredRolesText(profile.getPreferredRolesText());
        dto.setPreferredRoles(parsePreferredRoles(profile.getPreferredRolesText()));
        dto.setFavoriteHeroIds(profile.getFavoriteHeroes().stream().map(Hero::getId).toList());
        dto.setFavoriteHeroNames(profile.getFavoriteHeroes().stream().map(Hero::getName).toList());
        dto.setUsername(profile.getUser() != null ? profile.getUser().getUsername() : null);
        return dto;
    }

    private String toPreferredRolesText(List<String> preferredRoles) {
        List<String> normalizedRoles = DotaRolePreference.normalizeList(preferredRoles);
        if (normalizedRoles.isEmpty()) {
            return null;
        }

        StringJoiner joiner = new StringJoiner(", ");
        normalizedRoles.forEach(joiner::add);
        return joiner.toString();
    }

    private List<String> parsePreferredRoles(String preferredRolesText) {
        if (preferredRolesText == null || preferredRolesText.isBlank()) {
            return List.of();
        }

        return DotaRolePreference.normalizeList(List.of(preferredRolesText.split(",")));
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
