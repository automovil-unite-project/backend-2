package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.Report;
import com.automovil_unite.backend.domain.model.ReportStatus;
import com.automovil_unite.backend.domain.repository.ReportRepository;
import com.automovil_unite.backend.infrastructure.persistence.entity.RentalEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.ReportEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.mapper.PersistenceReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {
    
    private final JpaReportRepository jpaReportRepository;
    private final JpaRentalRepository jpaRentalRepository;
    private final JpaUserRepository jpaUserRepository;
    private final PersistenceReportMapper reportMapper = PersistenceReportMapper.INSTANCE;
    


    @Override
    public Report save(Report report) {
        ReportEntity entity;
        
        // Obtener el alquiler por ID
        RentalEntity rental = jpaRentalRepository.findById(report.getRentalId())
                .orElseThrow(() -> new IllegalArgumentException("Rental not found with id: " + report.getRentalId()));
        
        // Obtener el reportador por ID
        UserEntity reporter = jpaUserRepository.findById(report.getReporterId())
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found with id: " + report.getReporterId()));
        
        // Obtener el usuario reportado por ID
        UserEntity reportedUser = jpaUserRepository.findById(report.getReportedUserId())
                .orElseThrow(() -> new IllegalArgumentException("Reported user not found with id: " + report.getReportedUserId()));
        
        if (report.getId() != null) {
            entity = jpaReportRepository.findById(report.getId())
                    .orElse(new ReportEntity());
           // reportMapper.updateEntity(entity, report);
            entity.setRental(rental);
            entity.setReporter(reporter);
            entity.setReportedUser(reportedUser);
        } else {
            entity = reportMapper.toEntity(report, rental, reporter, reportedUser);
        }
        
        return reportMapper.toDomain(jpaReportRepository.save(entity));
    }

    @Override
    public Optional<Report> findById(UUID id) {
        return jpaReportRepository.findById(id)
                .map(reportMapper::toDomain);
    }

    @Override
    public List<Report> findByReporterId(UUID reporterId) {
        UserEntity reporter = jpaUserRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found with id: " + reporterId));
        
        return jpaReportRepository.findByReporter(reporter).stream()
                .map(reportMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByReportedUserId(UUID reportedUserId) {
        UserEntity reportedUser = jpaUserRepository.findById(reportedUserId)
                .orElseThrow(() -> new IllegalArgumentException("Reported user not found with id: " + reportedUserId));
        
        return jpaReportRepository.findByReportedUser(reportedUser).stream()
                .map(reportMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByStatus(ReportStatus status) {
        return jpaReportRepository.findByStatus(status).stream()
                .map(reportMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Report> findByRentalId(UUID rentalId) {
        RentalEntity rental = jpaRentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found with id: " + rentalId));
        
        return jpaReportRepository.findByRental(rental)
                .map(reportMapper::toDomain);
    }

    @Override
    public int countByReportedUserId(UUID reportedUserId) {
        return jpaReportRepository.countByReportedUserId(reportedUserId);
    }

    @Override
    public void delete(UUID id) {
        jpaReportRepository.deleteById(id);
    }
}