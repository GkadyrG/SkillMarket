package com.example.dotalink.feature.profile.service;

import com.example.dotalink.common.exception.ProfileNotFoundException;
import com.example.dotalink.feature.dotaaccount.repository.DotaAccountRepository;
import com.example.dotalink.feature.profile.dto.UserStatsDto;
import com.example.dotalink.feature.profile.model.UserProfile;
import com.example.dotalink.feature.profile.repository.UserProfileRepository;
import com.example.dotalink.feature.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserStatsService {

    private final UserProfileRepository userProfileRepository;
    private final DotaAccountRepository dotaAccountRepository;
    private final ReviewRepository reviewRepository;

    public UserStatsService(UserProfileRepository userProfileRepository,
                            DotaAccountRepository dotaAccountRepository,
                            ReviewRepository reviewRepository) {
        this.userProfileRepository = userProfileRepository;
        this.dotaAccountRepository = dotaAccountRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public UserStatsDto getStatsByUsername(String username) {
        UserProfile profile = userProfileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for user: " + username));

        UserStatsDto dto = new UserStatsDto();
        dto.setUsername(profile.getUser() != null ? profile.getUser().getUsername() : null);
        dto.setNickname(profile.getNickname());
        dto.setFavoriteHeroes(profile.getFavoriteHeroes().stream().map(h -> h.getName()).sorted().toList());

        var account = dotaAccountRepository.findByUserUsername(username).orElse(null);
        dto.setMmr(account != null ? account.getMmr() : null);

        Long targetUserId = profile.getUser() != null ? profile.getUser().getId() : null;
        if (targetUserId != null) {
            Double average = reviewRepository.findAverageRatingByTargetUserId(targetUserId);
            dto.setAverageRating(average == null ? 0.0 : average);
            dto.setReviewsCount(reviewRepository.findAllByTargetUserIdOrderByCreatedAtDesc(targetUserId).size());
        } else {
            dto.setAverageRating(0.0);
            dto.setReviewsCount(0);
        }

        return dto;
    }
}
