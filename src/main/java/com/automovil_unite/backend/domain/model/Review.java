package com.automovil_unite.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private UUID id;
    private UUID rentalId;
    private UUID reviewerId;
    private UUID targetId; // Puede ser un vehicleId o un userId dependiendo del tipo
    private ReviewType type;
    private int rating; // 1-5
    private String comment;
    private LocalDateTime createdAt;
}