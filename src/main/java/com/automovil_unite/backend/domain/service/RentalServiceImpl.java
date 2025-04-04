package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.event.DomainEventPublisher;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.exception.RentalStatusException;
import com.automovil_unite.backend.domain.model.Rental;
import com.automovil_unite.backend.domain.model.RentalStatus;
import com.automovil_unite.backend.domain.model.Vehicle;
import com.automovil_unite.backend.domain.repository.RentalRepository;
import com.automovil_unite.backend.domain.repository.VehicleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RentalServiceImpl implements RentalService {
    
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final UserService userService;
    private final DomainEventPublisher eventPublisher;
    private final SecureRandom random = new SecureRandom();
    
    public RentalServiceImpl(
            RentalRepository rentalRepository, 
            VehicleRepository vehicleRepository, 
            UserService userService, 
            DomainEventPublisher eventPublisher) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Rental createRental(Rental rental) {
        // Generar código de verificación para la entrega del vehículo
        rental.setVerificationCode(generateVerificationCode());
        
        // Si es contraoferta, actualizar el estado
        if (rental.getCounterOfferPrice() != null) {
            rental.setStatus(RentalStatus.COUNTER_OFFER);
        } else {
            rental.setStatus(RentalStatus.PENDING);
        }
        
        Rental savedRental = rentalRepository.save(rental);
        
        // Aquí se publicaría un evento de dominio para notificar la creación del alquiler
        // eventPublisher.publish(new RentalCreatedEvent(savedRental));
        
        return savedRental;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rental> findById(UUID id) {
        return rentalRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findByTenantId(UUID tenantId) {
        return rentalRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findByOwnerId(UUID ownerId) {
        return rentalRepository.findByOwnerId(ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findByVehicleId(UUID vehicleId) {
        return rentalRepository.findByVehicleId(vehicleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findByStatus(RentalStatus status) {
        return rentalRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public Rental updateRental(Rental rental) {
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public void deleteRental(UUID id) {
        rentalRepository.delete(id);
    }

    @Override
    @Transactional
    public Rental submitCounterOffer(UUID rentalId, BigDecimal counterOfferPrice) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "submit counter offer");
        }
        
        rental.setCounterOfferPrice(counterOfferPrice);
        rental.setStatus(RentalStatus.COUNTER_OFFER);
        
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental acceptRental(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (rental.getStatus() != RentalStatus.PENDING && rental.getStatus() != RentalStatus.COUNTER_OFFER) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "accept");
        }
        
        // Si hay una contraoferta, actualizar el precio final
        if (rental.getStatus() == RentalStatus.COUNTER_OFFER && rental.getCounterOfferPrice() != null) {
            rental.setFinalPrice(rental.getCounterOfferPrice());
        }
        
        rental.setStatus(RentalStatus.ACCEPTED);
        
        // Generar un nuevo código de verificación para la entrega
        rental.setVerificationCode(generateVerificationCode());
        
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental rejectRental(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (rental.getStatus() != RentalStatus.PENDING && rental.getStatus() != RentalStatus.COUNTER_OFFER) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "reject");
        }
        
        rental.setStatus(RentalStatus.REJECTED);
        
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental confirmRental(UUID rentalId, String verificationCode) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (rental.getStatus() != RentalStatus.ACCEPTED) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "confirm");
        }
        
        if (!rental.getVerificationCode().equals(verificationCode)) {
            throw new IllegalArgumentException("Invalid verification code");
        }
        
        rental.setVerificationCodeConfirmed(true);
        rental.setStatus(RentalStatus.CONFIRMED);
        
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental completeRental(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (rental.getStatus() != RentalStatus.CONFIRMED && rental.getStatus() != RentalStatus.IN_PROGRESS) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "complete");
        }
        
        rental.setStatus(RentalStatus.COMPLETED);
        rental.setActualEndDate(LocalDateTime.now());
        
        // Actualizar la fecha del último alquiler del vehículo
        vehicleRepository.findById(rental.getVehicleId()).ifPresent(vehicle -> {
            vehicle.setLastRentalEndDate(rental.getActualEndDate());
            vehicleRepository.save(vehicle);
        });
        
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental cancelRental(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (rental.getStatus() != RentalStatus.PENDING && 
            rental.getStatus() != RentalStatus.COUNTER_OFFER && 
            rental.getStatus() != RentalStatus.ACCEPTED) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "cancel");
        }
        
        rental.setStatus(RentalStatus.CANCELLED);
        
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental extendRental(UUID rentalId, LocalDateTime newEndDate) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        if (!rental.canExtend()) {
            throw new RentalStatusException(rentalId, rental.getStatus(), "extend");
        }
        
        if (!newEndDate.isAfter(rental.getEndDate())) {
            throw new IllegalArgumentException("New end date must be after current end date");
        }
        
        // Verificar si hay alquileres que se solapan en el nuevo período
        List<Rental> overlappingRentals = rentalRepository.findOverlappingRentals(
                rental.getVehicleId(), rental.getEndDate(), newEndDate);
        
        if (!overlappingRentals.isEmpty()) {
            throw new IllegalStateException("Vehicle is not available for the extended period");
        }
        
        LocalDateTime oldEndDate = rental.getEndDate();
        rental.setEndDate(newEndDate);
        rental.setStatus(RentalStatus.EXTENDED);
        
        // Recalcular el precio
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(rental.getVehicleId());
        if (vehicleOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            
            // Calcular el precio adicional por la extensión
            BigDecimal additionalPrice = vehicle.calculateRentalPrice(
                    oldEndDate, newEndDate, rental.isDiscountApplied());
            
            // Sumar al precio final
            rental.setFinalPrice(rental.getFinalPrice().add(additionalPrice));
        }
        
        Rental updatedRental = rentalRepository.save(rental);
        
        // Publicar evento de extensión
        // eventPublisher.publish(new RentalExtendedEvent(rentalId, oldEndDate, newEndDate));
        
        return updatedRental;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findLateRentals() {
        return rentalRepository.findLateRentals();
    }

    @Override
    @Transactional
    public void processLateRentals() {
        List<Rental> lateRentals = findLateRentals();
        
        for (Rental rental : lateRentals) {
            if (rental.isLate() && rental.isActive()) {
                // Cambiar estado a "en progreso" si estaba confirmado
                if (rental.getStatus() == RentalStatus.CONFIRMED) {
                    rental.setStatus(RentalStatus.IN_PROGRESS);
                    rentalRepository.save(rental);
                }
                
                // Aplicar penalización al usuario arrendatario
                userService.applyPenalty(rental.getTenantId(), rental.getId());
                
                // Marcar que se ha aplicado penalización
                rental.setPenaltyApplied(true);
                rentalRepository.save(rental);
                
                // Publicar evento de alquiler tardío
                // eventPublisher.publish(new RentalLateEvent(rental.getId(), rental.getTenantId(), rental.getVehicleId()));
            }
        }
    }

    @Override
    public String generateVerificationCode(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        String code = generateVerificationCode();
        rental.setVerificationCode(code);
        rentalRepository.save(rental);
        
        return code;
    }

    @Override
    public boolean validateVerificationCode(UUID rentalId, String code) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        return rental.getVerificationCode().equals(code);
    }
    
    private String generateVerificationCode() {
        // Generar un código aleatorio de 6 dígitos
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}