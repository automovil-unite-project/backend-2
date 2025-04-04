// Vehicle.java
package com.automovil_unite.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    private UUID id;
    private UUID ownerId;
    private String brand;
    private String model;
    private int year;
    private String plateNumber;
    private String color;
    private int seats;
    private String transmissionType;
    private String fuelType;
    private BigDecimal pricePerDay;
    private String description;
    private double rating;
    private int reviewCount;
    private int rentCount;
    private boolean available;
    private boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastRentalEndDate;
    
    @Builder.Default
    private Set<VehiclePhoto> photos = new HashSet<>();
    
    public boolean isAvailableForRent(LocalDateTime startDate, LocalDateTime endDate) {
        if (!available || !verified) {
            return false;
        }
        
        // Verificar si el vehículo no está disponible hasta un día después de su último alquiler
        if (lastRentalEndDate != null && 
            lastRentalEndDate.plusDays(1).isAfter(startDate)) {
            return false;
        }
        
        return true;
    }
    
    public BigDecimal calculateRentalPrice(LocalDateTime startDate, LocalDateTime endDate, boolean applyDiscount) {
        int days = (int) java.time.Duration.between(startDate, endDate).toDays();
        if (days < 1) days = 1; // Mínimo un día
        
        BigDecimal total = pricePerDay.multiply(BigDecimal.valueOf(days));
        
        if (applyDiscount) {
            // Aplicar descuento del 10%
            total = total.multiply(BigDecimal.valueOf(0.9));
        }
        
        return total;
    }
}