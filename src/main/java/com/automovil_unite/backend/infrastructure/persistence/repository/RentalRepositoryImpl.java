package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.Rental;
import com.automovil_unite.backend.domain.model.RentalStatus;
import com.automovil_unite.backend.domain.repository.RentalRepository;
import com.automovil_unite.backend.infrastructure.persistence.entity.RentalEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.VehicleEntity;
import com.automovil_unite.backend.infrastructure.persistence.mapper.PersistenceRentalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RentalRepositoryImpl implements RentalRepository {
    private final JpaRentalRepository jpaRentalRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaVehicleRepository jpaVehicleRepository;
    private final PersistenceRentalMapper rentalMapper = PersistenceRentalMapper.INSTANCE;

    @Override
    public Rental save(Rental rental) {
        RentalEntity entity;
        
        UserEntity tenant = jpaUserRepository.findById(rental.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id: " + rental.getTenantId()));
        
        UserEntity owner = jpaUserRepository.findById(rental.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + rental.getOwnerId()));
        
        VehicleEntity vehicle = jpaVehicleRepository.findById(rental.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + rental.getVehicleId()));

        if (rental.getId() != null) {
            entity = jpaRentalRepository.findById(rental.getId())
                    .orElse(new RentalEntity());
            rentalMapper.updateEntity(entity, rental);
            entity.setTenant(tenant);
            entity.setOwner(owner);
            entity.setVehicle(vehicle);
        } else {
            entity = rentalMapper.toEntity(rental, vehicle, tenant, owner);
        }
        return rentalMapper.toDomain(jpaRentalRepository.save(entity));
    }

    @Override
    public Optional<Rental> findById(UUID id) {
        return jpaRentalRepository.findById(id)
                .map(rentalMapper::toDomain);
    }

    @Override
    public List<Rental> findByTenantId(UUID tenantId) {
        UserEntity tenant = jpaUserRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id: " + tenantId));

        return jpaRentalRepository.findByTenant(tenant).stream()
                .map(rentalMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Rental> findByOwnerId(UUID ownerId) {
        UserEntity owner = jpaUserRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));

        return jpaRentalRepository.findByOwner(owner).stream()
                .map(rentalMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Rental> findByVehicleId(UUID vehicleId) {
        VehicleEntity vehicle = jpaVehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + vehicleId));

        return jpaRentalRepository.findByVehicle(vehicle).stream()
                .map(rentalMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Rental> findByStatus(RentalStatus status) {
        return jpaRentalRepository.findByStatus(status).stream()
                .map(rentalMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Rental> findOverlappingRentals(UUID vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRentalRepository.findOverlappingRentals(vehicleId, startDate, endDate).stream()
                .map(rentalMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Rental> findLateRentals() {
        return jpaRentalRepository.findLateRentals().stream()
                .map(rentalMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Rental> findByVerificationCode(String verificationCode) {
        return jpaRentalRepository.findByVerificationCode(verificationCode)
                .map(rentalMapper::toDomain);
    }

    @Override
    public void delete(UUID id) {
        jpaRentalRepository.deleteById(id);
    }
}