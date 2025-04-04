package com.automovil_unite.backend.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {
    private UUID id;
    private UUID ownerId;
    private String ownerName;
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
    private Set<VehiclePhotoDto> photos;
}
