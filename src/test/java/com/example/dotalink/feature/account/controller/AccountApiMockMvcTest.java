package com.example.dotalink.feature.account.controller;

import com.example.dotalink.feature.account.model.Role;
import com.example.dotalink.feature.account.model.User;
import com.example.dotalink.feature.account.model.UserProfile;
import com.example.dotalink.feature.account.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AccountApiMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUser() {
        if (userRepository.findByUsername("demo").isPresent()) {
            return;
        }

        User user = new User();
        user.setUsername("demo");
        user.setEmail("demo_api@example.com");
        user.setPasswordHash(passwordEncoder.encode("demo123"));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        UserProfile profile = new UserProfile();
        profile.setNickname("DemoAPI");
        profile.setUser(user);
        user.setProfile(profile);

        userRepository.save(user);
    }

    @Test
    void getApiDota_unauthorized_returns401Json() throws Exception {
        mockMvc.perform(get("/api/account/dota"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "demo", roles = {"USER"})
    void postApiDota_authenticated_returnsCreated() throws Exception {
        mockMvc.perform(post("/api/account/dota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "steamId": "76561198000000000",
                                  "accountId": "12345",
                                  "profileUrl": "https://steamcommunity.com/id/demo",
                                  "avatarUrl": "https://img.example/avatar.png"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.steamId").value("76561198000000000"));
    }
}
