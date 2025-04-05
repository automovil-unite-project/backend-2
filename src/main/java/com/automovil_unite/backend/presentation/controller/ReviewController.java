package com.automovil_unite.backend.presentation.controller;

import com.automovil_unite.backend.application.dto.ReviewCreateDto;
import com.automovil_unite.backend.application.dto.ReviewDto;
import com.automovil_unite.backend.application.service.ReviewApplicationService;
import com.automovil_unite.backend.domain.model.ReviewType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reseñas", description = "Gestión de reseñas de vehículos y usuarios")
@SecurityRequirement(name = "bearer-jwt")
public class ReviewController {

    private final ReviewApplicationService reviewService;

    @Autowired
    public ReviewController(ReviewApplicationService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Obtener reseñas de un vehículo", description = "Recupera todas las reseñas de un vehículo específico")
    public ResponseEntity<List<ReviewDto>> getVehicleReviews(@PathVariable UUID vehicleId) {
        List<ReviewDto> reviews = reviewService.getReviewsByTargetIdAndType(vehicleId, ReviewType.VEHICLE);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener reseñas de un usuario", description = "Recupera todas las reseñas de un usuario específico")
    public ResponseEntity<List<ReviewDto>> getUserReviews(@PathVariable UUID userId) {
        List<ReviewDto> reviews = reviewService.getReviewsByTargetIdAndType(userId, ReviewType.TENANT);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/rental/{rentalId}")
    @Operation(summary = "Obtener reseñas de un alquiler", description = "Recupera las reseñas asociadas a un alquiler específico")
    public ResponseEntity<ReviewDto> getRentalReview(
            @PathVariable UUID rentalId,
            @RequestParam ReviewType type) {
        ReviewDto review = reviewService.getReviewByRentalIdAndType(rentalId, type);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/my-reviews")
    @Operation(summary = "Obtener mis reseñas", description = "Recupera las reseñas escritas por el usuario actual")
    public ResponseEntity<List<ReviewDto>> getMyReviews() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID reviewerId = UUID.fromString(authentication.getName());
        
        List<ReviewDto> reviews = reviewService.getReviewsByReviewerId(reviewerId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    @Operation(summary = "Crear reseña", description = "Crea una nueva reseña para un vehículo o usuario después de un alquiler")
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewCreateDto createDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID reviewerId = UUID.fromString(authentication.getName());
        
        ReviewDto createdReview = reviewService.createReview(reviewerId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reseña", description = "Elimina una reseña (solo el autor puede eliminarla)")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rating/vehicle/{vehicleId}")
    @Operation(summary = "Obtener calificación promedio de un vehículo", description = "Recupera la calificación promedio de un vehículo específico")
    public ResponseEntity<Double> getVehicleAverageRating(@PathVariable UUID vehicleId) {
        double rating = reviewService.calculateAverageRating(vehicleId, ReviewType.VEHICLE);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/rating/user/{userId}")
    @Operation(summary = "Obtener calificación promedio de un usuario", description = "Recupera la calificación promedio de un usuario específico")
    public ResponseEntity<Double> getUserAverageRating(@PathVariable UUID userId) {
        double rating = reviewService.calculateAverageRating(userId, ReviewType.TENANT);
        return ResponseEntity.ok(rating);
    }
}