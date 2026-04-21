package com.example.dotalink.feature.review.service;

import com.example.dotalink.common.exception.AccessDeniedBusinessException;
import com.example.dotalink.common.exception.UserNotFoundException;
import com.example.dotalink.feature.review.dto.ReviewCreateDto;
import com.example.dotalink.feature.review.dto.ReviewViewDto;
import com.example.dotalink.feature.review.model.Review;
import com.example.dotalink.feature.review.repository.ReviewRepository;
import com.example.dotalink.feature.user.model.User;
import com.example.dotalink.feature.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createReview(String authorUsername, String targetUsername, ReviewCreateDto form) {
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authorUsername));
        User target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + targetUsername));

        if (author.getId().equals(target.getId())) {
            throw new AccessDeniedBusinessException("You cannot leave a review for yourself");
        }

        Review review = new Review();
        review.setAuthor(author);
        review.setTargetUser(target);
        review.setRating(form.getRating());
        review.setComment(clean(form.getComment()));

        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewViewDto> getReviewsForUser(String targetUsername) {
        User target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + targetUsername));

        return reviewRepository.findAllByTargetUserIdOrderByCreatedAtDesc(target.getId()).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public double getAverageRatingForUser(String targetUsername) {
        User target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + targetUsername));

        Double avg = reviewRepository.findAverageRatingByTargetUserId(target.getId());
        return avg == null ? 0.0 : avg;
    }

    private ReviewViewDto toDto(Review review) {
        ReviewViewDto dto = new ReviewViewDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setAuthorUsername(review.getAuthor() != null ? review.getAuthor().getUsername() : null);
        return dto;
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
