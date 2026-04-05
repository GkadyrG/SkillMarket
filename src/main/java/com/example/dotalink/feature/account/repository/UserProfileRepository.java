package com.example.dotalink.feature.account.repository;

import com.example.dotalink.feature.account.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);

    Optional<UserProfile> findByUserUsername(String username);

    @Query("""
            SELECT p
            FROM UserProfile p
            WHERE (:nickname = '' OR LOWER(p.nickname) LIKE CONCAT('%', :nickname, '%'))
              AND (:rank = '' OR LOWER(p.rank) = :rank)
              AND (:region = '' OR LOWER(p.region) = :region)
            """)
    Page<UserProfile> searchByJpql(
            @Param("nickname") String nickname,
            @Param("rank") String rank,
            @Param("region") String region,
            Pageable pageable
    );
}
