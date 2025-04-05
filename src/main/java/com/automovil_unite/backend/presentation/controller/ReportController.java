package com.automovil_unite.backend.presentation.controller;

import com.automovil_unite.backend.application.dto.ReportCreateDto;
import com.automovil_unite.backend.application.dto.ReportDto;
import com.automovil_unite.backend.application.service.ReportApplicationService;
import com.automovil_unite.backend.domain.model.ReportStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reportes", description = "Gestión de reportes de problemas con usuarios")
@SecurityRequirement(name = "bearer-jwt")
public class ReportController {

    private final ReportApplicationService reportService;

    @Autowired
    public ReportController(ReportApplicationService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los reportes", description = "Recupera la lista de todos los reportes (solo administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportDto>> getAllReports() {
        List<ReportDto> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reporte por ID", description = "Recupera los detalles de un reporte específico")
    public ResponseEntity<ReportDto> getReportById(@PathVariable UUID id) {
        ReportDto report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener reportes por estado", description = "Recupera los reportes que tienen un estado específico (solo administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportDto>> getReportsByStatus(@PathVariable ReportStatus status) {
        List<ReportDto> reports = reportService.getReportsByStatus(status);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/my-reports")
    @Operation(summary = "Obtener mis reportes", description = "Recupera los reportes creados por el usuario actual")
    public ResponseEntity<List<ReportDto>> getMyReports() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID reporterId = UUID.fromString(authentication.getName());
        
        List<ReportDto> reports = reportService.getReportsByReporterId(reporterId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/against-me")
    @Operation(summary = "Obtener reportes contra mí", description = "Recupera los reportes hechos contra el usuario actual")
    public ResponseEntity<List<ReportDto>> getReportsAgainstMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID reportedUserId = UUID.fromString(authentication.getName());
        
        List<ReportDto> reports = reportService.getReportsByReportedUserId(reportedUserId);
        return ResponseEntity.ok(reports);
    }

    @PostMapping
    @Operation(summary = "Crear reporte", description = "Crea un nuevo reporte de problema con un usuario")
    public ResponseEntity<ReportDto> createReport(@Valid @RequestBody ReportCreateDto createDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID reporterId = UUID.fromString(authentication.getName());
        
        ReportDto createdReport = reportService.createReport(reporterId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
    }

    @PostMapping("/{id}/resolve")
    @Operation(summary = "Resolver reporte", description = "Marca un reporte como resuelto (aceptado o rechazado) (solo administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportDto> resolveReport(
            @PathVariable UUID id,
            @RequestParam boolean accept) {
        ReportDto resolvedReport = reportService.resolveReport(id, accept);
        return ResponseEntity.ok(resolvedReport);
    }

    @GetMapping("/count/{userId}")
    @Operation(summary = "Contar reportes contra un usuario", description = "Cuenta el número de reportes hechos contra un usuario específico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> countReportsByReportedUserId(@PathVariable UUID userId) {
        int count = reportService.countByReportedUserId(userId);
        return ResponseEntity.ok(count);
    }
}