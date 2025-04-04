package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.PenaltyType;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserPenaltyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaUserPenaltyRepository extends JpaRepository<UserPenaltyEntity, UUID> {
    List<UserPenaltyEntity> findByUser(UserEntity user);
    
    @Query("SELECT p FROM UserPenaltyEntity p WHERE p.user.id = :userId AND p.active = true AND p.endDate > :now")
    List<UserPenaltyEntity> findActiveByUserId(UUID userId, LocalDateTime now);
    
    @Query("SELECT p FROM UserPenaltyEntity p WHERE p.user.id = :userId AND p.type = :type AND p.active = true AND p.endDate > :now")
    List<UserPenaltyEntity> findActiveByUserIdAndType(UUID userId, PenaltyType type, LocalDateTime now);
}
