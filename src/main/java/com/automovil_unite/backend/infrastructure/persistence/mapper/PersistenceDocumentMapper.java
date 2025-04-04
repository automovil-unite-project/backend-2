package com.automovil_unite.backend.infrastructure.persistence.mapper;

import com.automovil_unite.backend.domain.model.Document;
import com.automovil_unite.backend.infrastructure.persistence.entity.DocumentEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersistenceDocumentMapper {
    PersistenceDocumentMapper INSTANCE = Mappers.getMapper(PersistenceDocumentMapper.class);
    
    @Mapping(target = "userId", source = "user.id")
    Document toDomain(DocumentEntity entity);
    
    @Mapping(target = "user", ignore = true)
    DocumentEntity toEntity(Document domain);
    
    default DocumentEntity toEntity(Document domain, UserEntity user) {
        DocumentEntity entity = toEntity(domain);
        entity.setUser(user);
        return entity;
    }
}