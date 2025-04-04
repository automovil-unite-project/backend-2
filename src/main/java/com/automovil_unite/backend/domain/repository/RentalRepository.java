package com.automovil_unite.backend.domain.repository;

import com.automovil_unite.backend.domain.model.Rental;
import com.automovil_unite.backend.domain.model.RentalStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalRepository {
    Rental save(Rental rental);
    Optional<Rental> findById(UUID id);
    List<Rental> findByTenantId(UUID tenantId);
    List<Rental> findByOwnerId(UUID ownerId);
    List<Rental> findByVehicleId(UUID vehicleId);
    List<Rental> findByStatus(RentalStatus status);
    List<Rental> findOverlappingRentals(UUID vehicleId, LocalDateTime startDate, LocalDateTime endDate);
    List<Rental> findLateRentals();
    Optional<Rental> findByVerificationCode(String verificationCode);
    void delete(UUID id);
}