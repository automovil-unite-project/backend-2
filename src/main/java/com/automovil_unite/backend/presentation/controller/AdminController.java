//package com.automovil_unite.backend.presentation.controller;
//
//import com.automovil_unite.backend.application.dto.*;
//import com.automovil_unite.backend.application.service.*;
//import com.automovil_unite.backend.domain.model.ReportStatus;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/v1/admin")
//@Tag(name = "Administración", description = "Endpoints de administración del sistema")
//@SecurityRequirement(name = "bearer-jwt")
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminController {
//
//    private final UserApplicationService userService;
//    private final VehicleApplicationService vehicleService;
//    private final DocumentApplicationService documentService;
//    private final ReportApplicationService reportService;
//    private final StatisticsApplicationService statisticsService;
//
//    @Autowired
//    public AdminController(
//            UserApplicationService userService,
//            VehicleApplicationService vehicleService,
//            DocumentApplicationService documentService,
//            ReportApplicationService reportService,
//            StatisticsApplicationService statisticsService) {
//        this.userService = userService;
//        this.vehicleService = vehicleService;
//        this.documentService = documentService;
//        this.reportService = reportService;
//        this.statisticsService = statisticsService;
//    }
//
//    @GetMapping("/dashboard")
//    @Operation(summary = "Dashboard administrativo", description = "Obtiene estadísticas generales para el panel de administración")
//    public ResponseEntity<Map<String, Object>> getDashboard() {
//        Map<String, Object> dashboardData = statisticsService.getAdminDashboard();
//        return ResponseEntity.ok(dashboardData);
//    }
//
//    @GetMapping("/users")
//    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios del sistema")
//    public ResponseEntity<List<UserDto>> getAllUsers() {
//        List<UserDto> users = userService.getAllUsers();
//        return ResponseEntity.ok(users);
//    }
//
//    @GetMapping("/users/{id}")
//    @Operation(summary = "Detalles de usuario", description = "Obtiene los detalles de un usuario específico")
//    public ResponseEntity<UserDto> getUserDetails(@PathVariable UUID id) {
//        UserDto user = userService.getUserById(id);
//        return ResponseEntity.ok(user);
//    }
//
//    @PostMapping("/users/{id}/enable")
//    @Operation(summary = "Habilitar/deshabilitar usuario", description = "Habilita o deshabilita un usuario")
//    public ResponseEntity<UserDto> toggleUserStatus(
//            @PathVariable UUID id,
//            @RequestParam boolean enabled) {
//        UserDto user = userService.toggleUserStatus(id, enabled);
//        return ResponseEntity.ok(user);
//    }
//
//    @GetMapping("/vehicles/pending")
//    @Operation(summary = "Vehículos pendientes", description = "Obtiene vehículos pendientes de verificación")
//    public ResponseEntity<List<VehicleDto>> getPendingVehicles() {
//        List<VehicleDto> vehicles = vehicleService.getPendingVehicles();
//        return ResponseEntity.ok(vehicles);
//    }
//
//    @PostMapping("/vehicles/{id}/verify")
//    @Operation(summary = "Verificar vehículo", description = "Aprueba o rechaza un vehículo")
//    public ResponseEntity<VehicleDto> verifyVehicle(
//            @PathVariable UUID id,
//            @RequestParam boolean approved) {
//        VehicleDto vehicle = vehicleService.verifyVehicle(id, approved);
//        return ResponseEntity.ok(vehicle);
//    }
//
//    @GetMapping("/documents/pending")
//    @Operation(summary = "Documentos pendientes", description = "Obtiene documentos pendientes de verificación")
//    public ResponseEntity<List<DocumentDto>> getPendingDocuments() {
//        List<DocumentDto> documents = documentService.getPendingVerificationDocuments();
//        return ResponseEntity.ok(documents);
//    }
//
//    @PostMapping("/documents/{id}/verify")
//    @Operation(summary = "Verificar documento", description = "Aprueba o rechaza un documento")
//    public ResponseEntity<DocumentDto> verifyDocument(
//            @PathVariable UUID id,
//            @RequestParam boolean approved) {
//        DocumentDto document = documentService.verifyDocument(id, approved);
//        return ResponseEntity.ok(document);
//    }
//
//    @GetMapping("/reports/pending")
//    @Operation(summary = "Reportes pendientes", description = "Obtiene reportes pendientes de resolución")
//    public ResponseEntity<List<ReportDto>> getPendingReports() {
//        List<ReportDto> reports = reportService.getReportsByStatus(ReportStatus.PENDING);
//        return ResponseEntity.ok(reports);
//    }
//
//    @PostMapping("/reports/{id}/resolve")
//    @Operation(summary = "Resolver reporte", description = "Resuelve un reporte (aceptado o rechazado)")
//    public ResponseEntity<ReportDto> resolveReport(
//            @PathVariable UUID id,
//            @RequestParam boolean accept) {
//        ReportDto report = reportService.resolveReport(id, accept);
//        return ResponseEntity.ok(report);
//    }
//
//    @GetMapping("/statistics/rentals")
//    @Operation(summary = "Estadísticas de alquileres", description = "Obtiene estadísticas generales de alquileres")
//    public ResponseEntity<Map<String, Object>> getRentalStatistics() {
//        Map<String, Object> statistics = statisticsService.getSystemRentalStatistics();
//        return ResponseEntity.ok(statistics);
//    }
//    
//    @GetMapping("/statistics/users")
//    @Operation(summary = "Estadísticas de usuarios", description = "Obtiene estadísticas generales de usuarios")
//    public ResponseEntity<Map<String, Object>> getUserStatistics() {
//        Map<String, Object> statistics = statisticsService.getSystemUserStatistics();
//        return ResponseEntity.ok(statistics);
//    }
//}