package com.automovil_unite.backend.infrastructure.persistence.mapper;

import com.automovil_unite.backend.domain.model.UserPenalty;
import com.automovil_unite.backend.infrastructure.persistence.entity.RentalEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserPenaltyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersistenceUserPenaltyMapper {
    PersistenceUserPenaltyMapper INSTANCE = Mappers.getMapper(PersistenceUserPenaltyMapper.class);
    
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "rentalId", source = "rental.id")
    UserPenalty toDomain(UserPenaltyEntity entity);
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "rental", ignore = true)
    UserPenaltyEntity toEntity(UserPenalty domain);
    
    default UserPenaltyEntity toEntity(UserPenalty domain, UserEntity user, RentalEntity rental) {
        UserPenaltyEntity entity = toEntity(domain);
        entity.setUser(user);
        entity.setRental(rental);
        return entity;
    }
}
