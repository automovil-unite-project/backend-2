package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.ReviewType;
import com.automovil_unite.backend.infrastructure.persistence.entity.RentalEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.ReviewEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaReviewRepository extends JpaRepository<ReviewEntity, UUID> {
    List<ReviewEntity> findByReviewer(UserEntity reviewer);
    
    List<ReviewEntity> findByTargetIdAndType(UUID targetId, ReviewType type);
    
    Optional<ReviewEntity> findByRentalAndType(RentalEntity rental, ReviewType type);
    
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.targetId = :targetId AND r.type = :type")
    Double calculateAverageRating(UUID targetId, ReviewType type);
}
