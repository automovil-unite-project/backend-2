package com.automovil_unite.backend.application.dto;

import com.automovil_unite.backend.domain.model.ReviewType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Información de la reseña")
public class ReviewDto {
    @Schema(description = "Identificador único de la reseña", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Identificador único del alquiler", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID rentalId;

    @Schema(description = "Identificador único del autor de la reseña", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID reviewerId;

    @Schema(description = "Nombre del autor de la reseña", example = "Ana García")
    private String reviewerName;

    @Schema(description = "Identificador único del objetivo de la reseña (vehículo o usuario)", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID targetId;

    @Schema(description = "Tipo de reseña (VEHICLE=Vehículo, TENANT=Arrendatario)")
    private ReviewType type;

    @Schema(description = "Calificación (1-5)", example = "5")
    private int rating;

    @Schema(description = "Comentario de la reseña", example = "Excelente vehículo, muy limpio y en perfecto estado.")
    private String comment;

    @Schema(description = "Fecha de creación de la reseña")
    private LocalDateTime createdAt;
}
