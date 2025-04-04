package com.automovil_unite.backend.infrastructure.persistence.mapper;

import com.automovil_unite.backend.domain.model.VerificationCode;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.VerificationCodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersistenceVerificationCodeMapper {
    PersistenceVerificationCodeMapper INSTANCE = Mappers.getMapper(PersistenceVerificationCodeMapper.class);
    
    @Mapping(target = "userId", source = "user.id")
    VerificationCode toDomain(VerificationCodeEntity entity);
    
    @Mapping(target = "user", ignore = true)
    VerificationCodeEntity toEntity(VerificationCode domain);
    
    default VerificationCodeEntity toEntity(VerificationCode domain, UserEntity user) {
        VerificationCodeEntity entity = toEntity(domain);
        entity.setUser(user);
        return entity;
    }
}
