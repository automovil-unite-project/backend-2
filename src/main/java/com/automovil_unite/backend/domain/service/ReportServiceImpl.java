package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.event.DomainEventPublisher;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.model.Report;
import com.automovil_unite.backend.domain.model.ReportStatus;
import com.automovil_unite.backend.domain.repository.ReportRepository;
import com.automovil_unite.backend.domain.service.ReportService;
import com.automovil_unite.backend.domain.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {
    
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final DomainEventPublisher eventPublisher;
    
    public ReportServiceImpl(
            ReportRepository reportRepository,
            UserService userService,
            DomainEventPublisher eventPublisher) {
        this.reportRepository = reportRepository;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Report createReport(Report report) {
        // Validar que los usuarios existen
        userService.findById(report.getReporterId())
                .orElseThrow(() -> new EntityNotFoundException("Reporter", report.getReporterId().toString()));
        
        userService.findById(report.getReportedUserId())
                .orElseThrow(() -> new EntityNotFoundException("Reported user", report.getReportedUserId().toString()));
        
        // Establecer estado inicial
        report.setStatus(ReportStatus.PENDING);
        report.setResolved(false);
        
        Report savedReport = reportRepository.save(report);
        
        // Publicar evento
        // eventPublisher.publish(new ReportCreatedEvent(savedReport));
        
        return savedReport;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Report> findById(UUID id) {
        return reportRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> findByReporterId(UUID reporterId) {
        return reportRepository.findByReporterId(reporterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> findByReportedUserId(UUID reportedUserId) {
        return reportRepository.findByReportedUserId(reportedUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> findByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Report> findByRentalId(UUID rentalId) {
        return reportRepository.findByRentalId(rentalId);
    }

    @Override
    @Transactional
    public Report updateReport(Report report) {
        // Verificar que el reporte existe
        reportRepository.findById(report.getId())
                .orElseThrow(() -> new EntityNotFoundException("Report", report.getId().toString()));
        
        return reportRepository.save(report);
    }

    @Override
    @Transactional
    public void deleteReport(UUID id) {
        reportRepository.delete(id);
    }

    @Override
    @Transactional
    public Report resolveReport(UUID reportId, boolean accept) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report", reportId.toString()));
        
        if (report.isResolved()) {
            throw new IllegalStateException("Report is already resolved");
        }
        
        report.setResolved(true);
        report.setResolvedAt(LocalDateTime.now());
        report.setStatus(accept ? ReportStatus.ACCEPTED : ReportStatus.REJECTED);
        
        // Si el reporte es aceptado, incrementar el contador de reportes del usuario
        if (accept) {
            userService.applyPenalty(report.getReportedUserId(), report.getRentalId());
        }
        
        return reportRepository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public int countByReportedUserId(UUID reportedUserId) {
        return reportRepository.countByReportedUserId(reportedUserId);
    }
}