package com.automovil_unite.backend.domain.repository;

import com.automovil_unite.backend.domain.model.Vehicle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(UUID id);
    List<Vehicle> findByOwnerId(UUID ownerId);
    List<Vehicle> findAll();
    List<Vehicle> findTopRented(int limit);
    List<Vehicle> findTopByBrand(int limit);
    List<Vehicle> findAvailable(LocalDateTime startDate, LocalDateTime endDate);
    void delete(UUID id);
    boolean existsByPlateNumber(String plateNumber);
}