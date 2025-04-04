package com.automovil_unite.backend.application.service;

import com.automovil_unite.backend.application.dto.ReviewCreateDto;
import com.automovil_unite.backend.application.dto.ReviewDto;
import com.automovil_unite.backend.application.mapper.ReviewMapper;
import com.automovil_unite.backend.domain.event.DomainEventPublisher;
import com.automovil_unite.backend.domain.event.ReviewCreatedEvent;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.exception.ReviewException;
import com.automovil_unite.backend.domain.exception.UnauthorizedActionException;
import com.automovil_unite.backend.domain.model.Review;
import com.automovil_unite.backend.domain.model.ReviewType;
import com.automovil_unite.backend.domain.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewApplicationService {
    private final ReviewService reviewService;
    private final DomainEventPublisher eventPublisher;
    private final ReviewMapper reviewMapper;

    @Transactional
    public ReviewDto createReview(UUID reviewerId, ReviewCreateDto createDto) {
        // Check if user can review this rental
        if (!reviewService.canUserReview(reviewerId, createDto.getRentalId(), createDto.getType())) {
            throw new ReviewException(reviewerId, createDto.getRentalId(), 
                    "User cannot review this rental or has already reviewed it");
        }
        
        Review review = reviewMapper.toEntity(createDto);
        review.setReviewerId(reviewerId);
        
        Review savedReview = reviewService.createReview(review);
        
        // Publish domain event
        eventPublisher.publish(new ReviewCreatedEvent(savedReview));
        
        return reviewMapper.toDto(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewDto getReviewById(UUID id) {
        Review review = reviewService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review", id.toString()));
        return reviewMapper.toDto(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsByReviewerId(UUID reviewerId) {
        return reviewService.findByReviewerId(reviewerId).stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsByTargetIdAndType(UUID targetId, ReviewType type) {
        return reviewService.findByTargetIdAndType(targetId, type).stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewDto getReviewByRentalIdAndType(UUID rentalId, ReviewType type) {
        Review review = reviewService.findByRentalIdAndType(rentalId, type)
                .orElseThrow(() -> new EntityNotFoundException("Review not found for rental " + rentalId));
        return reviewMapper.toDto(review);
    }

    @Transactional(readOnly = true)
    public double calculateAverageRating(UUID targetId, ReviewType type) {
        return reviewService.calculateAverageRating(targetId, type);
    }

    @Transactional
    public void deleteReview(UUID id, UUID userId) {
        Review review = reviewService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review", id.toString()));
        
        // Only the reviewer can delete their review
        if (!review.getReviewerId().equals(userId)) {
            throw new UnauthorizedActionException(userId, "review", "delete");
        }
        
        reviewService.deleteReview(id);
    }
}
