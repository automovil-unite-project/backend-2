package com.automovil_unite.backend.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUpdateDto {
    private String color;
    
    @Min(value = 1, message = "Seats must be at least 1")
    private Integer seats;
    
    private String transmissionType;
    private String fuelType;
    
    @Positive(message = "Price must be positive")
    private BigDecimal pricePerDay;
    
    private String description;
    private Boolean available;
}
