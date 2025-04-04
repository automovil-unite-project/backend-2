package com.automovil_unite.backend.presentation.controller;


import com.automovil_unite.backend.application.dto.RentalCreateDto;
import com.automovil_unite.backend.application.dto.RentalDto;
import com.automovil_unite.backend.application.dto.RentalExtensionDto;
import com.automovil_unite.backend.application.dto.RentalVerificationDto;
import com.automovil_unite.backend.application.dto.ErrorResponseDto;

import com.automovil_unite.backend.application.service.RentalApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
@Tag(name = "Alquileres", description = "API para gestión de alquileres de vehículos")
@SecurityRequirement(name = "bearer-jwt")
public class RentalController {
    private final RentalApplicationService rentalService;

    @Operation(
            summary = "Obtener alquileres del usuario",
            description = "Recupera todos los alquileres asociados al usuario autenticado (como arrendatario o dueño)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alquileres recuperada correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RentalDto.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<RentalDto>> getUserRentals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        List<RentalDto> rentals;
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TENANT"))) {
            rentals = rentalService.getRentalsByTenantId(userId);
        } else {
            rentals = rentalService.getRentalsByOwnerId(userId);
        }
        
        return ResponseEntity.ok(rentals);
    }

    @Operation(
            summary = "Obtener alquiler por ID",
            description = "Recupera los detalles de un alquiler específico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alquiler encontrado",
                    content = @Content(schema = @Schema(implementation = RentalDto.class))),
            @ApiResponse(responseCode = "404", description = "Alquiler no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<RentalDto> getRentalById(
            @Parameter(description = "ID del alquiler", required = true)
            @PathVariable UUID id) {
        RentalDto rental = rentalService.getRentalById(id);
        return ResponseEntity.ok(rental);
    }

    @Operation(
            summary = "Crear solicitud de alquiler",
            description = "Crea una nueva solicitud de alquiler para un vehículo (solo para usuarios con rol TENANT)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitud de alquiler creada correctamente",
                    content = @Content(schema = @Schema(implementation = RentalDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de alquiler inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Solo roles TENANT",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Vehículo no disponible en las fechas solicitadas",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<RentalDto> createRental(
            @Parameter(description = "Datos para la solicitud de alquiler", required = true)
            @Valid @RequestBody RentalCreateDto createDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID tenantId = UUID.fromString(authentication.getName());
        
        RentalDto createdRental = rentalService.createRental(tenantId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRental);
    }

    @Operation(
            summary = "Enviar contraoferta",
            description = "Envía una contraoferta de precio para un alquiler pendiente (solo para el arrendatario)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraoferta enviada correctamente",
                    content = @Content(schema = @Schema(implementation = RentalDto.class))),
            @ApiResponse(responseCode = "400", description = "Precio de contraoferta inválido o estado de alquiler incorrecto",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - No es el arrendatario",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Alquiler no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/{id}/counter-offer")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<RentalDto> submitCounterOffer(
            @Parameter(description = "ID del alquiler", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Precio de contraoferta", required = true)
            @RequestParam BigDecimal counterOfferPrice) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID tenantId = UUID.fromString(authentication.getName());
        
        RentalDto updatedRental = rentalService.submitCounterOffer(id, tenantId, counterOfferPrice);
        return ResponseEntity.ok(updatedRental);
    }

    @Operation(
            summary = "Aceptar solicitud de alquiler",
            description = "Acepta una solicitud de alquiler o contraoferta (solo para el dueño del vehículo)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud aceptada correctamente",
                    content = @Content(schema = @Schema(implementation = RentalDto.class))),
            @ApiResponse(responseCode = "400", description = "Estado de alquiler incorrecto",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - No es el dueño",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Alquiler no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RentalDto> acceptRental(
            @Parameter(description = "ID del alquiler", required = true)
            @PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID ownerId = UUID.fromString(authentication.getName());
        
        RentalDto updatedRental = rentalService.acceptRental(id, ownerId);
        return ResponseEntity.ok(updatedRental);
    }

    @Operation(
            summary = "Rechazar solicitud de alquiler",
            description = "Rechaza una solicitud de alquiler o contraoferta (solo para el dueño del vehículo)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud rechazada correctamente",
                    content = @Content(schema = @Schema(implementation = RentalDto.class))),
            @ApiResponse(responseCode = "400", description = "Estado de alquiler incorrecto",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - No es el dueño",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Alquiler no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RentalDto> rejectRental(
            @Parameter(description = "ID del alquiler", required = true)
            @PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID ownerId = UUID.fromString(authentication.getName());
        
        RentalDto updatedRental = rentalService.rejectRental(id, ownerId);
        return ResponseEntity.ok(updatedRental);
    }

    @Operation(
            summary = "Confirmar entrega de vehículo",
            description = "Confirma la entrega del vehículo mediante un código de verificación"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrega confirmada correctamente",
                    content = @Content(schema = @Schema(implementation = RentalDto.class))),
            @ApiResponse(responseCode = "400", description = "Código inválido o estado de alquiler incorrecto",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Alquiler no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/{id}/confirm")
    public ResponseEntity<RentalDto> confirmRental(
            @Parameter(description = "ID del alquiler", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Código de verificación", required = true)
            @Valid @RequestBody RentalVerificationDto verificationDto) {
        RentalDto updatedRental = rentalService.confirmRental(id, verificationDto.getVerificationCode());
        return ResponseEntity.ok(updatedRental);
    }

    @Operation(
            summary = "Completar alquiler",
            description = "Marca un alquiler como completado después de la devolución del vehículo (solo para el dueño)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alquiler completado correctamente",
                    content = @Content(schema = @Schema(implementation = RentalDto.class))),
            @ApiResponse(responseCode = "400", description = "Estado de alquiler incorrecto",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - No es el dueño",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Alquiler no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<RentalDto> completeRental(
            @Parameter(description = "ID del alquiler", required = true)
            @PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID ownerId = UUID.fromString(authentication.getName());
        
        RentalDto updatedRental = rentalService.completeRental(id, ownerId);
        return ResponseEntity.ok(updatedRental);
    }

    @Operation(
            summary = "Cancelar alquiler",
            description = "Cancela un alquiler pendiente o aceptado (puede ser realizado por el arrendatario o el dueño)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alquiler cancelado correctamente",
                    content = @Content(schema = @Schema(implementation = RentalDto.class))),
            @ApiResponse(responseCode = "400", description = "Estado de alquiler incorrecto",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - No es ni el arrendatario ni el dueño",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Alquiler no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<RentalDto> cancelRental(
            @Parameter(description = "ID del alquiler", required = true)
            @PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        RentalDto updatedRental = rentalService.cancelRental(id, userId);
        return ResponseEntity.ok(updatedRental);
    }

    @Operation(
            summary = "Extender alquiler",
            description = "Extiende un alquiler a una nueva fecha de finalización (solo para el arrendatario)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alquiler extendido correctamente",
                    content = @Content(schema = @Schema(implementation = RentalDto.class))),
            @ApiResponse(responseCode = "400", description = "Fecha inválida o estado de alquiler incorrecto",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - No es el arrendatario",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Alquiler no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Vehículo no disponible para la extensión",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/{id}/extend")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<RentalDto> extendRental(
            @Parameter(description = "ID del alquiler", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Datos de extensión con nueva fecha de finalización", required = true)
            @Valid @RequestBody RentalExtensionDto extensionDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID tenantId = UUID.fromString(authentication.getName());
        
        RentalDto updatedRental = rentalService.extendRental(id, tenantId, extensionDto);
        return ResponseEntity.ok(updatedRental);
    }
}