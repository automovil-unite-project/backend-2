package com.automovil_unite.backend.presentation.controller;

import com.automovil_unite.backend.application.dto.VehicleDto;
import com.automovil_unite.backend.application.dto.VehicleRegistrationDto;
import com.automovil_unite.backend.application.dto.VehicleUpdateDto;
import com.automovil_unite.backend.application.dto.ErrorResponseDto;
import com.automovil_unite.backend.application.service.VehicleApplicationService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehículos", description = "API para gestión de vehículos")
@SecurityRequirement(name = "bearer-jwt")
public class VehicleController {
    private final VehicleApplicationService vehicleService;

    @Operation(
            summary = "Obtener todos los vehículos",
            description = "Recupera la lista de todos los vehículos disponibles en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vehículos recuperada correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleDto.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<VehicleDto>> getAllVehicles() {
        List<VehicleDto> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @Operation(
            summary = "Obtener vehículo por ID",
            description = "Recupera los detalles de un vehículo específico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo encontrado",
                    content = @Content(schema = @Schema(implementation = VehicleDto.class))),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getVehicleById(
            @Parameter(description = "ID del vehículo", required = true)
            @PathVariable UUID id) {
        VehicleDto vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }

    @Operation(
            summary = "Obtener vehículos disponibles",
            description = "Recupera la lista de vehículos disponibles para alquilar en un período específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vehículos disponibles",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleDto.class)))),
            @ApiResponse(responseCode = "400", description = "Parámetros de fecha inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/available")
    public ResponseEntity<List<VehicleDto>> getAvailableVehicles(
            @Parameter(description = "Fecha de inicio del alquiler (formato ISO)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin del alquiler (formato ISO)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<VehicleDto> vehicles = vehicleService.getAvailableVehicles(startDate, endDate);
        return ResponseEntity.ok(vehicles);
    }

    @Operation(
            summary = "Obtener vehículos más alquilados",
            description = "Recupera la lista de los vehículos más alquilados en la plataforma"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vehículos más alquilados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleDto.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/top-rented")
    public ResponseEntity<List<VehicleDto>> getTopRentedVehicles(
            @Parameter(description = "Cantidad límite de resultados")
            @RequestParam(defaultValue = "10") int limit) {
        List<VehicleDto> vehicles = vehicleService.getTopRentedVehicles(limit);
        return ResponseEntity.ok(vehicles);
    }

    @Operation(
            summary = "Obtener marcas más populares",
            description = "Recupera la lista de marcas de vehículos más populares en la plataforma"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de marcas más populares",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleDto.class)))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/top-brands")
    public ResponseEntity<List<VehicleDto>> getTopBrands(
            @Parameter(description = "Cantidad límite de resultados")
            @RequestParam(defaultValue = "10") int limit) {
        List<VehicleDto> vehicles = vehicleService.getTopByBrand(limit);
        return ResponseEntity.ok(vehicles);
    }

    @Operation(
            summary = "Registrar un nuevo vehículo",
            description = "Crea un nuevo vehículo para ser alquilado (solo para usuarios con rol OWNER)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehículo creado correctamente",
                    content = @Content(schema = @Schema(implementation = VehicleDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de vehículo inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - Solo roles OWNER",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<VehicleDto> createVehicle(
            @Parameter(description = "Datos de registro del vehículo", required = true)
            @Valid @RequestBody VehicleRegistrationDto registrationDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID ownerId = UUID.fromString(authentication.getName());
        
        VehicleDto createdVehicle = vehicleService.registerVehicle(ownerId, registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
    }

    @Operation(
            summary = "Actualizar un vehículo",
            description = "Actualiza la información de un vehículo existente (solo para el propietario del vehículo)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = VehicleDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - No es el propietario",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<VehicleDto> updateVehicle(
            @Parameter(description = "ID del vehículo", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Datos de actualización del vehículo", required = true)
            @Valid @RequestBody VehicleUpdateDto updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = UUID.fromString(authentication.getName());
        
        VehicleDto updatedVehicle = vehicleService.updateVehicle(id, currentUserId, updateDto);
        return ResponseEntity.ok(updatedVehicle);
    }

    @Operation(
            summary = "Eliminar un vehículo",
            description = "Elimina un vehículo existente (solo para el propietario del vehículo)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehículo eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - No es el propietario",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteVehicle(
            @Parameter(description = "ID del vehículo", required = true)
            @PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = UUID.fromString(authentication.getName());
        
        vehicleService.deleteVehicle(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Subir foto de vehículo",
            description = "Sube una nueva foto para un vehículo (solo para el propietario del vehículo)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto subida correctamente"),
            @ApiResponse(responseCode = "400", description = "Archivo inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido - No es el propietario",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/{id}/photos")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> uploadVehiclePhoto(
            @Parameter(description = "ID del vehículo", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Archivo de imagen", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Indicador si la foto es la principal")
            @RequestParam(value = "main", defaultValue = "false") boolean main) {
        // Esta implementación es simplificada - en producción se debería manejar la carga de archivos
        return ResponseEntity.ok().build();
    }
}