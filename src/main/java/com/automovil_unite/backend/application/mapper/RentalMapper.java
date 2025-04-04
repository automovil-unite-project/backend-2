package com.automovil_unite.backend.application.mapper;

import com.automovil_unite.backend.application.dto.RentalCreateDto;
import com.automovil_unite.backend.application.dto.RentalDto;
import com.automovil_unite.backend.domain.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {VehicleMapper.class})
public interface RentalMapper {
    RentalMapper INSTANCE = Mappers.getMapper(RentalMapper.class);
    
    RentalDto toDto(Rental rental);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "actualEndDate", ignore = true)
    @Mapping(target = "verificationCode", ignore = true)
    @Mapping(target = "verificationCodeConfirmed", constant = "false")
    @Mapping(target = "originalPrice", ignore = true)
    @Mapping(target = "finalPrice", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "discountApplied", constant = "false")
    @Mapping(target = "penaltyApplied", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Rental toEntity(RentalCreateDto dto);
}
