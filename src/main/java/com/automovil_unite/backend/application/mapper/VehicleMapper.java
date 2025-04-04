package com.automovil_unite.backend.application.mapper;

import com.automovil_unite.backend.application.dto.VehicleDto;
import com.automovil_unite.backend.application.dto.VehicleRegistrationDto;
import com.automovil_unite.backend.application.dto.VehicleUpdateDto;
import com.automovil_unite.backend.domain.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {VehiclePhotoMapper.class})
public interface VehicleMapper {
    VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);
    
    VehicleDto toDto(Vehicle vehicle);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "rentCount", ignore = true)
    @Mapping(target = "available", constant = "true")
    @Mapping(target = "verified", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastRentalEndDate", ignore = true)
    @Mapping(target = "photos", ignore = true)
    Vehicle toEntity(VehicleRegistrationDto dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "plateNumber", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "rentCount", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastRentalEndDate", ignore = true)
    @Mapping(target = "photos", ignore = true)
    void updateEntity(@MappingTarget Vehicle vehicle, VehicleUpdateDto dto);
}
