package com.automovil_unite.backend.application.mapper;

import com.automovil_unite.backend.application.dto.ReportCreateDto;
import com.automovil_unite.backend.application.dto.ReportDto;
import com.automovil_unite.backend.domain.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReportMapper {
    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);
    
    ReportDto toDto(Report report);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reporterId", ignore = true)
    @Mapping(target = "resolved", constant = "false")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    Report toEntity(ReportCreateDto dto);
}
