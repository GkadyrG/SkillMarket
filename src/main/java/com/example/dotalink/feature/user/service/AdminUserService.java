package com.example.dotalink.feature.user.service;

import com.example.dotalink.feature.user.repository.UserRepository;

@Deprecated
public class AdminUserService extends UserService {

    public AdminUserService(UserRepository userRepository) {
        super(userRepository);
    }
}
