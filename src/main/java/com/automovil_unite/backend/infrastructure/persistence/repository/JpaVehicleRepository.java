package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaVehicleRepository extends JpaRepository<VehicleEntity, UUID> {
    List<VehicleEntity> findByOwner(UserEntity owner);
    
    List<VehicleEntity> findByAvailableAndVerified(boolean available, boolean verified);
    
    boolean existsByPlateNumber(String plateNumber);
    
    @Query("SELECT v FROM VehicleEntity v ORDER BY v.rentCount DESC")
    List<VehicleEntity> findTopRented();
    
    @Query("SELECT v.brand, COUNT(v) as count FROM VehicleEntity v GROUP BY v.brand ORDER BY count DESC")
    List<Object[]> findTopBrands();
    
    @Query("SELECT v FROM VehicleEntity v WHERE v.available = true AND v.verified = true " +
           "AND (v.lastRentalEndDate IS NULL OR v.lastRentalEndDate < :startDate) " +
           "AND NOT EXISTS (SELECT r FROM RentalEntity r WHERE r.vehicle = v AND r.status IN ('ACCEPTED', 'CONFIRMED', 'IN_PROGRESS', 'EXTENDED') " +
           "AND ((r.startDate <= :startDate AND r.endDate >= :startDate) OR " +
           "(r.startDate <= :endDate AND r.endDate >= :endDate) OR " +
           "(r.startDate >= :startDate AND r.endDate <= :endDate)))")
    List<VehicleEntity> findAvailableVehicles(LocalDateTime startDate, LocalDateTime endDate);
}
