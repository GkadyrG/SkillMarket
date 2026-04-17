package com.example.dotalink.feature.partypost.repository;

import com.example.dotalink.feature.partypost.model.PartyPost;
import com.example.dotalink.feature.partypost.model.PartyPostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartyPostRepository extends JpaRepository<PartyPost, Long> {

    @EntityGraph(attributePaths = {"author"})
    @Query("""
            SELECT p
            FROM PartyPost p
            WHERE (:rank = '' OR LOWER(p.requiredRank) = :rank)
              AND (:role = '' OR LOWER(p.roleNeeded) = :role)
              AND (:region = '' OR LOWER(p.region) = :region)
              AND p.status = :status
            """)
    Page<PartyPost> searchOpenPosts(
            @Param("rank") String rank,
            @Param("role") String role,
            @Param("region") String region,
            @Param("status") PartyPostStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"author"})
    Optional<PartyPost> findWithAuthorById(Long id);
}
