package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.event.DomainEventPublisher;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.model.Review;
import com.automovil_unite.backend.domain.model.ReviewType;
import com.automovil_unite.backend.domain.model.RentalStatus;
import com.automovil_unite.backend.domain.repository.RentalRepository;
import com.automovil_unite.backend.domain.repository.ReviewRepository;
import com.automovil_unite.backend.domain.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final RentalRepository rentalRepository;
    private final DomainEventPublisher eventPublisher;
    
    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            RentalRepository rentalRepository,
            DomainEventPublisher eventPublisher) {
        this.reviewRepository = reviewRepository;
        this.rentalRepository = rentalRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Review createReview(Review review) {
        // Validar que el alquiler existe
        rentalRepository.findById(review.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("Rental", review.getRentalId().toString()));
        
        // Validar que el usuario puede hacer esta reseña
        if (!canUserReview(review.getReviewerId(), review.getRentalId(), review.getType())) {
            throw new IllegalStateException("User cannot review this rental or has already reviewed it");
        }
        
        Review savedReview = reviewRepository.save(review);
        
        // Publicar evento
        // eventPublisher.publish(new ReviewCreatedEvent(savedReview));
        
        return savedReview;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findById(UUID id) {
        return reviewRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findByReviewerId(UUID reviewerId) {
        return reviewRepository.findByReviewerId(reviewerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findByTargetIdAndType(UUID targetId, ReviewType type) {
        return reviewRepository.findByTargetIdAndType(targetId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findByRentalIdAndType(UUID rentalId, ReviewType type) {
        return reviewRepository.findByRentalIdAndType(rentalId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateAverageRating(UUID targetId, ReviewType type) {
        return reviewRepository.calculateAverageRating(targetId, type);
    }

    @Override
    @Transactional
    public Review updateReview(Review review) {
        // Verificar que la reseña existe
        reviewRepository.findById(review.getId())
                .orElseThrow(() -> new EntityNotFoundException("Review", review.getId().toString()));
        
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(UUID id) {
        reviewRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserReview(UUID userId, UUID rentalId, ReviewType type) {
        // Verificar que el alquiler esté completado
        return rentalRepository.findById(rentalId)
                .map(rental -> {
                    // El alquiler debe estar completado
                    if (rental.getStatus() != RentalStatus.COMPLETED) {
                        return false;
                    }
                    
                    // Verificar que el usuario es parte del alquiler
                    boolean isUserInvolved = false;
                    if (type == ReviewType.VEHICLE) {
                        // Para reseñas de vehículos, el usuario debe ser el arrendatario
                        isUserInvolved = rental.getTenantId().equals(userId);
                    } else if (type == ReviewType.TENANT) {
                        // Para reseñas de arrendatarios, el usuario debe ser el dueño
                        isUserInvolved = rental.getOwnerId().equals(userId);
                    }
                    
                    if (!isUserInvolved) {
                        return false;
                    }
                    
                    // Verificar que no haya hecho una reseña ya
                    return !reviewRepository.findByRentalIdAndType(rentalId, type).isPresent();
                })
                .orElse(false);
    }
}