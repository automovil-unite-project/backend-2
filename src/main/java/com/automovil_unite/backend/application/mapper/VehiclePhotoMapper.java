package com.automovil_unite.backend.application.mapper;

import com.automovil_unite.backend.application.dto.VehiclePhotoDto;
import com.automovil_unite.backend.domain.model.VehiclePhoto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehiclePhotoMapper {
    VehiclePhotoMapper INSTANCE = Mappers.getMapper(VehiclePhotoMapper.class);
    
    VehiclePhotoDto toDto(VehiclePhoto photo);
    VehiclePhoto toEntity(VehiclePhotoDto dto);
}
