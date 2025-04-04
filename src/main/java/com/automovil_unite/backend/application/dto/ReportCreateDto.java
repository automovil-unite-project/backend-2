package com.automovil_unite.backend.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportCreateDto {
    @NotNull(message = "Rental ID is required")
    private UUID rentalId;
    
    @NotNull(message = "Reported user ID is required")
    private UUID reportedUserId;
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    private String description;
}
