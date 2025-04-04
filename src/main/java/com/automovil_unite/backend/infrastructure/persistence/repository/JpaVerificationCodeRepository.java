package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.VerificationCodeType;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.VerificationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaVerificationCodeRepository extends JpaRepository<VerificationCodeEntity, UUID> {
    Optional<VerificationCodeEntity> findByUserAndTypeAndUsedFalseAndExpiresAtAfter(
            UserEntity user, VerificationCodeType type, LocalDateTime now);
    
    Optional<VerificationCodeEntity> findByCodeAndTypeAndUsedFalseAndExpiresAtAfter(
            String code, VerificationCodeType type, LocalDateTime now);
    
    @Modifying
    @Query("UPDATE VerificationCodeEntity v SET v.used = true, v.usedAt = :now WHERE v.id = :id")
    void markAsUsed(UUID id, LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM VerificationCodeEntity v WHERE v.expiresAt < :now OR v.used = true")
    void deleteExpiredCodes(LocalDateTime now);
}
