package com.example.dotalink.feature.review.repository;

import com.example.dotalink.feature.review.model.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"author"})
    List<Review> findAllByTargetUserIdOrderByCreatedAtDesc(Long targetUserId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetUser.id = :targetUserId")
    Double findAverageRatingByTargetUserId(@Param("targetUserId") Long targetUserId);
}
