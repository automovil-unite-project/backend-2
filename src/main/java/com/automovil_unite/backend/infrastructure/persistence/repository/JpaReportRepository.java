package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.ReportStatus;
import com.automovil_unite.backend.infrastructure.persistence.entity.RentalEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.ReportEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaReportRepository extends JpaRepository<ReportEntity, UUID> {
    List<ReportEntity> findByReporter(UserEntity reporter);
    
    List<ReportEntity> findByReportedUser(UserEntity reportedUser);
    
    List<ReportEntity> findByStatus(ReportStatus status);
    
    Optional<ReportEntity> findByRental(RentalEntity rental);
    
    @Query("SELECT COUNT(r) FROM ReportEntity r WHERE r.reportedUser.id = :reportedUserId")
    int countByReportedUserId(UUID reportedUserId);
}
