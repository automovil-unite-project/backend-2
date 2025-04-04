package com.automovil_unite.backend.application.mapper;

import com.automovil_unite.backend.application.dto.DocumentDto;
import com.automovil_unite.backend.domain.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DocumentMapper {
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);
    
    DocumentDto toDto(Document document);
    Document toEntity(DocumentDto dto);
}
