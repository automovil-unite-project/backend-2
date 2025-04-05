package com.automovil_unite.backend.presentation.controller;

import com.automovil_unite.backend.application.dto.StatisticsDto;
import com.automovil_unite.backend.application.dto.VehicleDto;
import com.automovil_unite.backend.application.service.RentalApplicationService;
import com.automovil_unite.backend.application.service.StatisticsApplicationService;
import com.automovil_unite.backend.application.service.VehicleApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/statistics")
@Tag(name = "Estadísticas", description = "Endpoint para estadísticas y dashboard")
@SecurityRequirement(name = "bearer-jwt")
public class StatisticsController {

    private final VehicleApplicationService vehicleService;
    private final RentalApplicationService rentalService;
    private final StatisticsApplicationService statisticsService;

    @Autowired
    public StatisticsController(
            VehicleApplicationService vehicleService,
            RentalApplicationService rentalService,
            StatisticsApplicationService statisticsService) {
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/home")
    @Operation(summary = "Estadísticas para el dashboard",
               description = "Recupera las estadísticas principales para mostrar en el dashboard")
    public ResponseEntity<StatisticsDto> getHomeStatistics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());

        StatisticsDto statistics = statisticsService.getHomeStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/top-rented-vehicles")
    @Operation(summary = "Vehículos más alquilados",
               description = "Recupera la lista de vehículos más alquilados")
    public ResponseEntity<List<VehicleDto>> getTopRentedVehicles(
            @RequestParam(defaultValue = "5") int limit) {
        List<VehicleDto> vehicles = vehicleService.getTopRentedVehicles(limit);
        return ResponseEntity.ok(vehicles);
    }

//    @GetMapping("/top-brands")
//    @Operation(summary = "Marcas más populares",
//               description = "Recupera las marcas de vehículos más populares")
//    public ResponseEntity<Map<String, Integer>> getTopBrands() {
//        Map<String, Integer> brands = statisticsService.getTopBrands();
//        return ResponseEntity.ok(brands);
//    }
//
//    @GetMapping("/user")
//    @Operation(summary = "Estadísticas del usuario",
//               description = "Recupera estadísticas específicas del usuario autenticado")
//    public ResponseEntity<Map<String, Object>> getUserStatistics() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString(authentication.getName());
//
//        Map<String, Object> statistics = statisticsService.getUserStatistics(userId);
//        return ResponseEntity.ok(statistics);
//    }
//
//    @GetMapping("/rentals/monthly")
//    @Operation(summary = "Alquileres mensuales",
//               description = "Recupera estadísticas de alquileres agrupados por mes")
//    public ResponseEntity<Map<String, Integer>> getMonthlyRentalsStatistics() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString(authentication.getName());
//
//        Map<String, Integer> statistics = statisticsService.getMonthlyRentalsStatistics(userId);
//        return ResponseEntity.ok(statistics);
//    }
//
//    @GetMapping("/income/monthly")
//    @Operation(summary = "Ingresos mensuales",
//               description = "Recupera estadísticas de ingresos agrupados por mes (solo para dueños)")
//    public ResponseEntity<Map<String, Double>> getMonthlyIncomeStatistics() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID ownerId = UUID.fromString(authentication.getName());
//
//        Map<String, Double> statistics = statisticsService.getMonthlyIncomeStatistics(ownerId);
//        return ResponseEntity.ok(statistics);
//    }
}