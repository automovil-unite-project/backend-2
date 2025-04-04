package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.RentalStatus;
import com.automovil_unite.backend.infrastructure.persistence.entity.RentalEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRentalRepository extends JpaRepository<RentalEntity, UUID> {
    List<RentalEntity> findByTenant(UserEntity tenant);

    List<RentalEntity> findByOwner(UserEntity owner);

    List<RentalEntity> findByVehicle(VehicleEntity vehicle);

    List<RentalEntity> findByStatus(RentalStatus status);

    Optional<RentalEntity> findByVerificationCode(String verificationCode);

    @Query("SELECT r FROM RentalEntity r WHERE r.vehicle.id = :vehicleId " +
            "AND r.status IN ('ACCEPTED', 'CONFIRMED', 'IN_PROGRESS', 'EXTENDED') " +
            "AND ((r.startDate <= :startDate AND r.endDate >= :startDate) OR " +
            "(r.startDate <= :endDate AND r.endDate >= :endDate) OR " +
            "(r.startDate >= :startDate AND r.endDate <= :endDate))")
    List<RentalEntity> findOverlappingRentals(UUID vehicleId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT r FROM RentalEntity r WHERE r.status = 'IN_PROGRESS' " +
            "AND r.endDate < CURRENT_TIMESTAMP")
    List<RentalEntity> findLateRentals();
}
