package com.example.dotalink.feature.review.repository;

import com.example.dotalink.feature.review.model.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    interface ReviewAnalyticsProjection {
        String getUsername();

        String getNickname();

        Double getAverageRating();

        Long getReviewsCount();
    }

    @EntityGraph(attributePaths = {"author"})
    List<Review> findAllByTargetUserIdOrderByCreatedAtDesc(Long targetUserId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetUser.id = :targetUserId")
    Double findAverageRatingByTargetUserId(@Param("targetUserId") Long targetUserId);

    @Query(value = """
            WITH user_rating AS (
                SELECT
                    r.target_user_id AS target_user_id,
                    AVG(r.rating) AS average_rating,
                    COUNT(*) AS reviews_count
                FROM reviews r
                GROUP BY r.target_user_id
            ),
            platform_avg AS (
                SELECT AVG(ur.average_rating) AS platform_average
                FROM user_rating ur
            )
            SELECT
                u.username AS username,
                up.nickname AS nickname,
                ur.average_rating AS averageRating,
                ur.reviews_count AS reviewsCount
            FROM user_rating ur
            JOIN users u ON u.id = ur.target_user_id
            LEFT JOIN user_profiles up ON up.user_id = u.id
            CROSS JOIN platform_avg pa
            WHERE ur.reviews_count >= :minReviews
              AND ur.average_rating > pa.platform_average
            ORDER BY ur.average_rating DESC, ur.reviews_count DESC
            """, nativeQuery = true)
    List<ReviewAnalyticsProjection> findUsersAbovePlatformAverage(@Param("minReviews") long minReviews);
}
