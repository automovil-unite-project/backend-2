package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.Review;
import com.automovil_unite.backend.domain.model.ReviewType;
import com.automovil_unite.backend.domain.repository.ReviewRepository;
import com.automovil_unite.backend.infrastructure.persistence.entity.RentalEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.ReviewEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.mapper.PersistenceReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {
    
    private final JpaReviewRepository jpaReviewRepository;
    private final JpaRentalRepository jpaRentalRepository;
    private final JpaUserRepository jpaUserRepository;
    private final PersistenceReviewMapper reviewMapper = PersistenceReviewMapper.INSTANCE;
    


    @Override
    public Review save(Review review) {
        ReviewEntity entity;
        
        // Obtener el alquiler por ID
        RentalEntity rental = jpaRentalRepository.findById(review.getRentalId())
                .orElseThrow(() -> new IllegalArgumentException("Rental not found with id: " + review.getRentalId()));
        
        // Obtener el revisor por ID
        UserEntity reviewer = jpaUserRepository.findById(review.getReviewerId())
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found with id: " + review.getReviewerId()));
        
        if (review.getId() != null) {
            entity = jpaReviewRepository.findById(review.getId())
                    .orElse(new ReviewEntity());
            //reviewMapper.updateEntity(entity, review);
            entity.setRental(rental);
            entity.setReviewer(reviewer);
        } else {
            entity = reviewMapper.toEntity(review, rental, reviewer);
        }
        
        return reviewMapper.toDomain(jpaReviewRepository.save(entity));
    }

    @Override
    public Optional<Review> findById(UUID id) {
        return jpaReviewRepository.findById(id)
                .map(reviewMapper::toDomain);
    }

    @Override
    public List<Review> findByReviewerId(UUID reviewerId) {
        UserEntity reviewer = jpaUserRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found with id: " + reviewerId));
        
        return jpaReviewRepository.findByReviewer(reviewer).stream()
                .map(reviewMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByTargetIdAndType(UUID targetId, ReviewType type) {
        return jpaReviewRepository.findByTargetIdAndType(targetId, type).stream()
                .map(reviewMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Review> findByRentalIdAndType(UUID rentalId, ReviewType type) {
        RentalEntity rental = jpaRentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found with id: " + rentalId));
        
        return jpaReviewRepository.findByRentalAndType(rental, type)
                .map(reviewMapper::toDomain);
    }

    @Override
    public double calculateAverageRating(UUID targetId, ReviewType type) {
        Double average = jpaReviewRepository.calculateAverageRating(targetId, type);
        return average != null ? average : 0.0;
    }

    @Override
    public void delete(UUID id) {
        jpaReviewRepository.deleteById(id);
    }
}