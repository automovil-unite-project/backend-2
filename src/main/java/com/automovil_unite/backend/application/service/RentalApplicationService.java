package com.automovil_unite.backend.application.service;

import com.automovil_unite.backend.application.dto.RentalCreateDto;
import com.automovil_unite.backend.application.dto.RentalDto;
import com.automovil_unite.backend.application.dto.RentalExtensionDto;
import com.automovil_unite.backend.application.mapper.RentalMapper;
import com.automovil_unite.backend.domain.event.DomainEventPublisher;
import com.automovil_unite.backend.domain.event.RentalCreatedEvent;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.exception.RentalExtensionException;
import com.automovil_unite.backend.domain.exception.RentalStatusException;
import com.automovil_unite.backend.domain.exception.UnauthorizedActionException;
import com.automovil_unite.backend.domain.exception.UserPenaltyException;
import com.automovil_unite.backend.domain.exception.VehicleNotAvailableException;
import com.automovil_unite.backend.domain.model.Rental;
import com.automovil_unite.backend.domain.model.RentalStatus;
import com.automovil_unite.backend.domain.model.Vehicle;
import com.automovil_unite.backend.domain.service.RentalService;
import com.automovil_unite.backend.domain.service.UserService;
import com.automovil_unite.backend.domain.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentalApplicationService {
    private final RentalService rentalService;
    private final VehicleService vehicleService;
    private final UserService userService;
    private final DomainEventPublisher eventPublisher;
    private final RentalMapper rentalMapper;

    @Transactional
    public RentalDto createRental(UUID tenantId, RentalCreateDto createDto) {
        // Check if user is eligible for rental (no active penalties)
        if (!userService.isUserEligibleForRental(tenantId)) {
            throw new UserPenaltyException(tenantId);
        }
        
        // Check if vehicle exists
        Vehicle vehicle = vehicleService.findById(createDto.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", createDto.getVehicleId().toString()));
        
        // Check if vehicle is available for the requested period
        if (!vehicleService.isVehicleAvailable(createDto.getVehicleId(), 
                                             createDto.getStartDate(), 
                                             createDto.getEndDate())) {
            throw new VehicleNotAvailableException(createDto.getVehicleId(), 
                                                createDto.getStartDate(), 
                                                createDto.getEndDate());
        }
        
        // Create rental entity
        Rental rental = rentalMapper.toEntity(createDto);
        rental.setTenantId(tenantId);
        rental.setOwnerId(vehicle.getOwnerId());
        
        // Calculate price
        boolean discountEligible = userService.isUserEligibleForDiscount(tenantId);
        BigDecimal calculatedPrice = vehicle.calculateRentalPrice(
                createDto.getStartDate(), createDto.getEndDate(), discountEligible);
        
        rental.setOriginalPrice(calculatedPrice);
        rental.setFinalPrice(calculatedPrice);
        rental.setDiscountApplied(discountEligible);
        
        // If counter offer price is provided
        if (createDto.getCounterOfferPrice() != null) {
            rental.setCounterOfferPrice(createDto.getCounterOfferPrice());
            rental.setStatus(RentalStatus.COUNTER_OFFER);
        }
        
        Rental savedRental = rentalService.createRental(rental);
        
        // Publish domain event
        eventPublisher.publish(new RentalCreatedEvent(savedRental));
        
        return rentalMapper.toDto(savedRental);
    }

    @Transactional(readOnly = true)
    public RentalDto getRentalById(UUID id) {
        Rental rental = rentalService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rental", id.toString()));
        return rentalMapper.toDto(rental);
    }

    @Transactional(readOnly = true)
    public List<RentalDto> getRentalsByTenantId(UUID tenantId) {
        return rentalService.findByTenantId(tenantId).stream()
                .map(rentalMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RentalDto> getRentalsByOwnerId(UUID ownerId) {
        return rentalService.findByOwnerId(ownerId).stream()
                .map(rentalMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalDto submitCounterOffer(UUID rentalId, UUID tenantId, BigDecimal counterOfferPrice) {
        Rental rental = rentalService.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (!rental.getTenantId().equals(tenantId)) {
            throw new UnauthorizedActionException(tenantId, "rental", "submit counter offer");
        }
        
        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "submit counter offer");
        }
        
        Rental updatedRental = rentalService.submitCounterOffer(rentalId, counterOfferPrice);
        return rentalMapper.toDto(updatedRental);
    }

    @Transactional
    public RentalDto acceptRental(UUID rentalId, UUID ownerId) {
        Rental rental = rentalService.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (!rental.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedActionException(ownerId, "rental", "accept");
        }
        
        if (rental.getStatus() != RentalStatus.PENDING && rental.getStatus() != RentalStatus.COUNTER_OFFER) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "accept");
        }
        
        Rental updatedRental = rentalService.acceptRental(rentalId);
        
        // Generate verification code
        String verificationCode = rentalService.generateVerificationCode(rentalId);
        
        return rentalMapper.toDto(updatedRental);
    }

    @Transactional
    public RentalDto rejectRental(UUID rentalId, UUID ownerId) {
        Rental rental = rentalService.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (!rental.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedActionException(ownerId, "rental", "reject");
        }
        
        if (rental.getStatus() != RentalStatus.PENDING && rental.getStatus() != RentalStatus.COUNTER_OFFER) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "reject");
        }
        
        Rental updatedRental = rentalService.rejectRental(rentalId);
        return rentalMapper.toDto(updatedRental);
    }

    @Transactional
    public RentalDto confirmRental(UUID rentalId, String verificationCode) {
        Rental rental = rentalService.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (rental.getStatus() != RentalStatus.ACCEPTED) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "confirm");
        }
        
        Rental updatedRental = rentalService.confirmRental(rentalId, verificationCode);
        return rentalMapper.toDto(updatedRental);
    }

    @Transactional
    public RentalDto completeRental(UUID rentalId, UUID ownerId) {
        Rental rental = rentalService.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (!rental.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedActionException(ownerId, "rental", "complete");
        }
        
        if (rental.getStatus() != RentalStatus.CONFIRMED && rental.getStatus() != RentalStatus.IN_PROGRESS) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "complete");
        }
        
        Rental updatedRental = rentalService.completeRental(rentalId);
        return rentalMapper.toDto(updatedRental);
    }

    @Transactional
    public RentalDto cancelRental(UUID rentalId, UUID userId) {
        Rental rental = rentalService.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        // Both tenant and owner can cancel
        if (!rental.getTenantId().equals(userId) && !rental.getOwnerId().equals(userId)) {
            throw new UnauthorizedActionException(userId, "rental", "cancel");
        }
        
        // Can only cancel if rental is pending, counter offer, or accepted
        if (rental.getStatus() != RentalStatus.PENDING && 
            rental.getStatus() != RentalStatus.COUNTER_OFFER && 
            rental.getStatus() != RentalStatus.ACCEPTED) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "cancel");
        }
        
        Rental updatedRental = rentalService.cancelRental(rentalId);
        return rentalMapper.toDto(updatedRental);
    }

    @Transactional
    public RentalDto extendRental(UUID rentalId, UUID tenantId, RentalExtensionDto extensionDto) {
        Rental rental = rentalService.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        // Only tenant can extend
        if (!rental.getTenantId().equals(tenantId)) {
            throw new UnauthorizedActionException(tenantId, "rental", "extend");
        }
        
        // Check if rental can be extended
        if (!rental.canExtend()) {
            throw new RentalExtensionException(rentalId, "Rental is not in a state that can be extended");
        }
        
        // Check if new end date is after current end date
        if (!extensionDto.getNewEndDate().isAfter(rental.getEndDate())) {
            throw new RentalExtensionException(rentalId, "New end date must be after current end date");
        }
        
        // Check if vehicle is available for the extended period
        if (!vehicleService.isVehicleAvailable(
                rental.getVehicleId(), 
                rental.getEndDate(), 
                extensionDto.getNewEndDate())) {
            throw new VehicleNotAvailableException(
                    rental.getVehicleId(), 
                    rental.getEndDate(), 
                    extensionDto.getNewEndDate());
        }
        
        Rental updatedRental = rentalService.extendRental(rentalId, extensionDto.getNewEndDate());
        return rentalMapper.toDto(updatedRental);
    }

    @Transactional
    public void processLateRentals() {
        rentalService.processLateRentals();
    }
}
