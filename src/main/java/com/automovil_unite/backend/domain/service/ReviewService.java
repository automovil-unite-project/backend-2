package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.model.Review;
import com.automovil_unite.backend.domain.model.ReviewType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewService {
    Review createReview(Review review);
    Optional<Review> findById(UUID id);
    List<Review> findByReviewerId(UUID reviewerId);
    List<Review> findByTargetIdAndType(UUID targetId, ReviewType type);
    Optional<Review> findByRentalIdAndType(UUID rentalId, ReviewType type);
    double calculateAverageRating(UUID targetId, ReviewType type);
    Review updateReview(Review review);
    void deleteReview(UUID id);
    boolean canUserReview(UUID userId, UUID rentalId, ReviewType type);
}