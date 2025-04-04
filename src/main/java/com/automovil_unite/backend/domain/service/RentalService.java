package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.model.Rental;
import com.automovil_unite.backend.domain.model.RentalStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalService {
    Rental createRental(Rental rental);
    Optional<Rental> findById(UUID id);
    List<Rental> findByTenantId(UUID tenantId);
    List<Rental> findByOwnerId(UUID ownerId);
    List<Rental> findByVehicleId(UUID vehicleId);
    List<Rental> findByStatus(RentalStatus status);
    Rental updateRental(Rental rental);
    void deleteRental(UUID id);
    Rental submitCounterOffer(UUID rentalId, BigDecimal counterOfferPrice);
    Rental acceptRental(UUID rentalId);
    Rental rejectRental(UUID rentalId);
    Rental confirmRental(UUID rentalId, String verificationCode);
    Rental completeRental(UUID rentalId);
    Rental cancelRental(UUID rentalId);
    Rental extendRental(UUID rentalId, LocalDateTime newEndDate);
    List<Rental> findLateRentals();
    void processLateRentals();
    String generateVerificationCode(UUID rentalId);
    boolean validateVerificationCode(UUID rentalId, String code);
}
