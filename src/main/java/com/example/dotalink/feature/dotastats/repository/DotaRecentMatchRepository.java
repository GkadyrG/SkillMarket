package com.example.dotalink.feature.dotastats.repository;

import com.example.dotalink.feature.dotastats.model.DotaRecentMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DotaRecentMatchRepository extends JpaRepository<DotaRecentMatch, Long> {
    List<DotaRecentMatch> findTop20ByAccountIdOrderByStartTimeDesc(Long accountId);

    long countByAccountId(Long accountId);

    void deleteByAccountId(Long accountId);
}
