package com.example.dotalink.feature.auth.service;

import com.example.dotalink.common.exception.DuplicateEmailException;
import com.example.dotalink.feature.account.model.User;
import com.example.dotalink.feature.account.repository.UserRepository;
import com.example.dotalink.feature.auth.dto.RegistrationForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void register_createsUserAndProfile() {
        String uid = UUID.randomUUID().toString().substring(0, 8);

        RegistrationForm form = new RegistrationForm();
        form.setUsername("user_" + uid);
        form.setEmail("user_" + uid + "@example.com");
        form.setPassword("secret123");
        form.setConfirmPassword("secret123");

        Long id = registrationService.register(form);

        User saved = userRepository.findById(id).orElseThrow();
        assertThat(saved.getProfile()).isNotNull();
        assertThat(saved.getPasswordHash()).isNotEqualTo("secret123");
    }

    @Test
    void register_throwsOnDuplicateEmail() {
        RegistrationForm form1 = new RegistrationForm();
        form1.setUsername("alpha");
        form1.setEmail("duplicate@example.com");
        form1.setPassword("secret123");
        form1.setConfirmPassword("secret123");
        registrationService.register(form1);

        RegistrationForm form2 = new RegistrationForm();
        form2.setUsername("beta");
        form2.setEmail("duplicate@example.com");
        form2.setPassword("secret123");
        form2.setConfirmPassword("secret123");

        assertThatThrownBy(() -> registrationService.register(form2))
                .isInstanceOf(DuplicateEmailException.class);
    }
}
