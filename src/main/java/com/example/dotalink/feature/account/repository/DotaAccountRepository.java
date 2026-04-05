package com.example.dotalink.feature.account.repository;

import com.example.dotalink.feature.account.model.DotaAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DotaAccountRepository extends JpaRepository<DotaAccount, Long> {
    Optional<DotaAccount> findByUserId(Long userId);

    Optional<DotaAccount> findByUserUsername(String username);
}
