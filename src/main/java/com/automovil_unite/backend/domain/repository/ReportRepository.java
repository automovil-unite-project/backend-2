package com.automovil_unite.backend.domain.repository;

import com.automovil_unite.backend.domain.model.Report;
import com.automovil_unite.backend.domain.model.ReportStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRepository {
    Report save(Report report);
    Optional<Report> findById(UUID id);
    List<Report> findByReporterId(UUID reporterId);
    List<Report> findByReportedUserId(UUID reportedUserId);
    List<Report> findByStatus(ReportStatus status);
    Optional<Report> findByRentalId(UUID rentalId);
    int countByReportedUserId(UUID reportedUserId);
    void delete(UUID id);
}