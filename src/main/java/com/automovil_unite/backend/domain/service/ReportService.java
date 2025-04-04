package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.model.Report;
import com.automovil_unite.backend.domain.model.ReportStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportService {
    Report createReport(Report report);
    Optional<Report> findById(UUID id);
    List<Report> findByReporterId(UUID reporterId);
    List<Report> findByReportedUserId(UUID reportedUserId);
    List<Report> findByStatus(ReportStatus status);
    Optional<Report> findByRentalId(UUID rentalId);
    Report updateReport(Report report);
    void deleteReport(UUID id);
    Report resolveReport(UUID reportId, boolean accept);
    int countByReportedUserId(UUID reportedUserId);
}