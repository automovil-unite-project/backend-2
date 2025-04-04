package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.model.Vehicle;
import com.automovil_unite.backend.domain.model.VehiclePhoto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleService {
    Vehicle registerVehicle(Vehicle vehicle);
    Optional<Vehicle> findById(UUID id);
    List<Vehicle> findByOwnerId(UUID ownerId);
    List<Vehicle> findAll();
    List<Vehicle> findTopRented(int limit);
    List<Vehicle> findTopByBrand(int limit);
    List<Vehicle> findAvailable(LocalDateTime startDate, LocalDateTime endDate);
    Vehicle updateVehicle(Vehicle vehicle);
    void deleteVehicle(UUID id);
    boolean isVehicleAvailable(UUID vehicleId, LocalDateTime startDate, LocalDateTime endDate);
    Vehicle addPhoto(UUID vehicleId, VehiclePhoto photo);
    Vehicle removePhoto(UUID vehicleId, UUID photoId);
    boolean verifyVehicle(UUID vehicleId, boolean approved);
}