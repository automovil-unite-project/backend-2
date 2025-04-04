package com.automovil_unite.backend.infrastructure.persistence.mapper;

import com.automovil_unite.backend.domain.model.Vehicle;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.VehicleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {PersistenceVehiclePhotoMapper.class})
public interface PersistenceVehicleMapper {
    PersistenceVehicleMapper INSTANCE = Mappers.getMapper(PersistenceVehicleMapper.class);
    
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "photos", ignore = true)
    Vehicle toDomain(VehicleEntity entity);
    
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "photos", ignore = true)
    VehicleEntity toEntity(Vehicle domain);
    
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "photos", ignore = true)
    void updateEntity(@MappingTarget VehicleEntity entity, Vehicle domain);
    
    default VehicleEntity toEntity(Vehicle domain, UserEntity owner) {
        VehicleEntity entity = toEntity(domain);
        entity.setOwner(owner);
        return entity;
    }
}
