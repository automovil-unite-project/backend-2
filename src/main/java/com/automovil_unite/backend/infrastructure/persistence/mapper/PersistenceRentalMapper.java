package com.automovil_unite.backend.infrastructure.persistence.mapper;

import com.automovil_unite.backend.domain.model.Rental;
import com.automovil_unite.backend.infrastructure.persistence.entity.RentalEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.VehicleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersistenceRentalMapper {
    PersistenceRentalMapper INSTANCE = Mappers.getMapper(PersistenceRentalMapper.class);
    
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "ownerId", source = "owner.id")
    Rental toDomain(RentalEntity entity);
    
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "owner", ignore = true)
    RentalEntity toEntity(Rental domain);
    
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateEntity(@MappingTarget RentalEntity entity, Rental domain);
    
    default RentalEntity toEntity(Rental domain, VehicleEntity vehicle, UserEntity tenant, UserEntity owner) {
        RentalEntity entity = toEntity(domain);
        entity.setVehicle(vehicle);
        entity.setTenant(tenant);
        entity.setOwner(owner);
        return entity;
    }
}
