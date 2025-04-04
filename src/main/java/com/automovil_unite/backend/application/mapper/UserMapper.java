package com.automovil_unite.backend.application.mapper;

import com.automovil_unite.backend.application.dto.UserDto;
import com.automovil_unite.backend.application.dto.UserRegistrationDto;
import com.automovil_unite.backend.application.dto.UserUpdateDto;
import com.automovil_unite.backend.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {DocumentMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    UserDto toDto(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "reportCount", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "identificationVerified", constant = "false")
    @Mapping(target = "criminalRecordVerified", constant = "false")
    @Mapping(target = "drivingLicenseVerified", constant = "false")
    @Mapping(target = "profilePhotoVerified", constant = "false")
    @Mapping(target = "documents", ignore = true)
    User toEntity(UserRegistrationDto dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "reportCount", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "identificationNumber", ignore = true)
    @Mapping(target = "identificationVerified", ignore = true)
    @Mapping(target = "criminalRecordVerified", ignore = true)
    @Mapping(target = "drivingLicenseVerified", ignore = true)
    @Mapping(target = "profilePhotoVerified", ignore = true)
    @Mapping(target = "documents", ignore = true)
    void updateEntity(@MappingTarget User user, UserUpdateDto dto);
}
