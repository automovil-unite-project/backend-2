package com.automovil_unite.backend.domain.repository;

import com.automovil_unite.backend.domain.model.Review;
import com.automovil_unite.backend.domain.model.ReviewType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository {
    Review save(Review review);
    Optional<Review> findById(UUID id);
    List<Review> findByReviewerId(UUID reviewerId);
    List<Review> findByTargetIdAndType(UUID targetId, ReviewType type);
    Optional<Review> findByRentalIdAndType(UUID rentalId, ReviewType type);
    double calculateAverageRating(UUID targetId, ReviewType type);
    void delete(UUID id);
}