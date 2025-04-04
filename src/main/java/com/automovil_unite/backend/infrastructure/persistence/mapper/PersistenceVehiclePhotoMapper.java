package com.automovil_unite.backend.infrastructure.persistence.mapper;

import com.automovil_unite.backend.domain.model.VehiclePhoto;
import com.automovil_unite.backend.infrastructure.persistence.entity.VehicleEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.VehiclePhotoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersistenceVehiclePhotoMapper {
    PersistenceVehiclePhotoMapper INSTANCE = Mappers.getMapper(PersistenceVehiclePhotoMapper.class);
    
    @Mapping(target = "vehicleId", source = "vehicle.id")
    VehiclePhoto toDomain(VehiclePhotoEntity entity);
    
    @Mapping(target = "vehicle", ignore = true)
    VehiclePhotoEntity toEntity(VehiclePhoto domain);
    
    default VehiclePhotoEntity toEntity(VehiclePhoto domain, VehicleEntity vehicle) {
        VehiclePhotoEntity entity = toEntity(domain);
        entity.setVehicle(vehicle);
        return entity;
    }
}
