package com.example.dotalink.feature.profile.service;

import com.example.dotalink.feature.profile.repository.UserProfileCriteriaRepository;
import com.example.dotalink.feature.profile.repository.UserProfileRepository;

@Deprecated
public class PlayerSearchService extends PlayerDirectoryService {

    public PlayerSearchService(UserProfileRepository userProfileRepository,
                               UserProfileCriteriaRepository userProfileCriteriaRepository) {
        super(userProfileRepository, userProfileCriteriaRepository);
    }
}
