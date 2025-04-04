package com.automovil_unite.backend.application.mapper;

import com.automovil_unite.backend.application.dto.NotificationDto;
import com.automovil_unite.backend.domain.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);
    
    NotificationDto toDto(Notification notification);
    Notification toEntity(NotificationDto dto);
}
