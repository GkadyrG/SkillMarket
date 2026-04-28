package com.example.dotalink.feature.dotastats.repository;

import com.example.dotalink.feature.dotastats.model.DotaPlayerHeroStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DotaPlayerHeroStatsRepository extends JpaRepository<DotaPlayerHeroStats, Long> {
    List<DotaPlayerHeroStats> findByAccountIdOrderByGamesDesc(Long accountId);

    long countByAccountId(Long accountId);

    void deleteByAccountId(Long accountId);
}
