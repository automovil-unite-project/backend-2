package com.automovil_unite.backend.application.dto;

import com.automovil_unite.backend.domain.model.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateDto {
    @NotNull(message = "Rental ID is required")
    private UUID rentalId;
    
    @NotNull(message = "Target ID is required")
    private UUID targetId;
    
    @NotNull(message = "Review type is required")
    private ReviewType type;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;
    
    private String comment;
}
