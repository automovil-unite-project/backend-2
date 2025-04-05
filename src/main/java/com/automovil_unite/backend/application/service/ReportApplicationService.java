package com.automovil_unite.backend.application.service;

import com.automovil_unite.backend.application.dto.ReportCreateDto;
import com.automovil_unite.backend.application.dto.ReportDto;
import com.automovil_unite.backend.application.mapper.ReportMapper;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.exception.UnauthorizedActionException;
import com.automovil_unite.backend.domain.model.Report;
import com.automovil_unite.backend.domain.model.ReportStatus;
import com.automovil_unite.backend.domain.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportApplicationService {
    private final ReportService reportService;
    private final ReportMapper reportMapper;

    @Transactional
    public ReportDto createReport(UUID reporterId, ReportCreateDto createDto) {
        Report report = reportMapper.toEntity(createDto);
        report.setReporterId(reporterId);
        
        Report savedReport = reportService.createReport(report);
        return reportMapper.toDto(savedReport);
    }

    @Transactional(readOnly = true)
    public ReportDto getReportById(UUID id) {
        Report report = reportService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Report", id.toString()));
        return reportMapper.toDto(report);
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getReportsByReporterId(UUID reporterId) {
        return reportService.findByReporterId(reporterId).stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getReportsByReportedUserId(UUID reportedUserId) {
        return reportService.findByReportedUserId(reportedUserId).stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReportDto> getAllReports() {
        return reportService.findByStatus(null).stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReportDto> getReportsByStatus(ReportStatus status) {
        return reportService.findByStatus(status).stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ReportDto resolveReport(UUID reportId, boolean accept) {
        Report resolvedReport = reportService.resolveReport(reportId, accept);
        return reportMapper.toDto(resolvedReport);
    }

    @Transactional
    public void deleteReport(UUID id, UUID userId) {
        Report report = reportService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Report", id.toString()));
        
        // Solo el creador del reporte puede eliminarlo (si no está resuelto)
        if (!report.getReporterId().equals(userId) && !report.isResolved()) {
            throw new UnauthorizedActionException(userId, "report", "delete");
        }
        
        reportService.deleteReport(id);
    }

    @Transactional(readOnly = true)
    public int countByReportedUserId(UUID reportedUserId) {
        return reportService.countByReportedUserId(reportedUserId);
    }
}