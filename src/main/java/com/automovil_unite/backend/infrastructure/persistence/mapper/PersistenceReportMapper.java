package com.automovil_unite.backend.infrastructure.persistence.mapper;

import com.automovil_unite.backend.domain.model.Report;
import com.automovil_unite.backend.infrastructure.persistence.entity.RentalEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.ReportEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersistenceReportMapper {
    PersistenceReportMapper INSTANCE = Mappers.getMapper(PersistenceReportMapper.class);
    
    @Mapping(target = "rentalId", source = "rental.id")
    @Mapping(target = "reporterId", source = "reporter.id")
    @Mapping(target = "reportedUserId", source = "reportedUser.id")
    Report toDomain(ReportEntity entity);
    
    @Mapping(target = "rental", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "reportedUser", ignore = true)
    ReportEntity toEntity(Report domain);
    
    default ReportEntity toEntity(Report domain, RentalEntity rental, UserEntity reporter, UserEntity reportedUser) {
        ReportEntity entity = toEntity(domain);
        entity.setRental(rental);
        entity.setReporter(reporter);
        entity.setReportedUser(reportedUser);
        return entity;
    }
}
