package com.example.dotalink.feature.account.service;

import com.example.dotalink.feature.account.dto.PlayerSearchFilter;
import com.example.dotalink.feature.account.model.UserProfile;
import com.example.dotalink.feature.account.repository.UserProfileCriteriaRepository;
import com.example.dotalink.feature.account.repository.UserProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class PlayerSearchService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileCriteriaRepository userProfileCriteriaRepository;

    public PlayerSearchService(UserProfileRepository userProfileRepository,
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

    private String sanitizeLower(String value) {
        String sanitized = sanitize(value);
        return sanitized == null ? null : sanitized.toLowerCase(Locale.ROOT);
    }

    private String sanitizeLowerOrEmpty(String value) {
        String sanitized = sanitize(value);
        return sanitized == null ? "" : sanitized.toLowerCase(Locale.ROOT);
    }
}
