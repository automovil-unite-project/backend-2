package com.automovil_unite.backend.infrastructure.persistence.mapper;

import com.automovil_unite.backend.domain.model.User;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {PersistenceDocumentMapper.class})
public interface PersistenceUserMapper {
    PersistenceUserMapper INSTANCE = Mappers.getMapper(PersistenceUserMapper.class);
    
    @Mapping(target = "documents", ignore = true)
    User toDomain(UserEntity entity);
    
    @Mapping(target = "documents", ignore = true)
    UserEntity toEntity(User domain);
    
    @Mapping(target = "documents", ignore = true)
    void updateEntity(@MappingTarget UserEntity entity, User domain);
}
