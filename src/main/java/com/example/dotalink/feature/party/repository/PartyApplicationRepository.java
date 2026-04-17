package com.example.dotalink.feature.application.repository;

import com.example.dotalink.feature.application.model.PartyApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartyApplicationRepository extends JpaRepository<PartyApplication, Long> {
    Optional<PartyApplication> findByPostIdAndApplicantId(Long postId, Long applicantId);

    List<PartyApplication> findAllByPostIdOrderByCreatedAtDesc(Long postId);

    long countByPostId(Long postId);
}
