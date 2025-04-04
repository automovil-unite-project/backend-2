package com.automovil_unite.backend.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRegistrationDto {
    @NotBlank(message = "Brand is required")
    private String brand;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be valid")
    private Integer year;
    
    @NotBlank(message = "Plate number is required")
    private String plateNumber;
    
    @NotBlank(message = "Color is required")
    private String color;
    
    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "Seats must be at least 1")
    private Integer seats;
    
    @NotBlank(message = "Transmission type is required")
    private String transmissionType;
    
    @NotBlank(message = "Fuel type is required")
    private String fuelType;
    
    @NotNull(message = "Price per day is required")
    @Positive(message = "Price must be positive")
    private BigDecimal pricePerDay;
    
    private String description;
}
