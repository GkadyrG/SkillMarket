package com.example.dotalink.feature.profile.service;

import com.example.dotalink.feature.profile.dto.PlayerSearchFilter;
import com.example.dotalink.feature.profile.model.UserProfile;
import com.example.dotalink.feature.profile.repository.UserProfileCriteriaRepository;
import com.example.dotalink.feature.profile.repository.UserProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class PlayerDirectoryService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileCriteriaRepository userProfileCriteriaRepository;

    public PlayerDirectoryService(UserProfileRepository userProfileRepository,
                                  UserProfileCriteriaRepository userProfileCriteriaRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileCriteriaRepository = userProfileCriteriaRepository;
    }

    @Transactional(readOnly = true)
    public Page<UserProfile> search(PlayerSearchFilter filter, Pageable pageable) {
        Page<UserProfile> page;

        if ("jpql".equalsIgnoreCase(filter.getMode())) {
            page = userProfileRepository.searchByJpql(
                    sanitizeLowerOrEmpty(filter.getNickname()),
                    sanitizeLowerOrEmpty(filter.getRank()),
                    sanitizeLowerOrEmpty(filter.getRegion()),
                    pageable
            );
        } else {
            page = userProfileCriteriaRepository.search(
                    sanitize(filter.getNickname()),
                    sanitize(filter.getRank()),
                    sanitize(filter.getRegion()),
                    pageable
            );
        }

        List<UserProfile> initialized = page.getContent();
        initialized.forEach(profile -> {
            if (profile.getUser() != null) {
                profile.getUser().getUsername();
            }
        });

        return new PageImpl<>(initialized, pageable, page.getTotalElements());
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String sanitizeLowerOrEmpty(String value) {
        String sanitized = sanitize(value);
        return sanitized == null ? "" : sanitized.toLowerCase(Locale.ROOT);
    }
}
