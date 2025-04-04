package com.automovil_unite.backend.infrastructure.persistence.mapper;

import com.automovil_unite.backend.domain.model.Notification;
import com.automovil_unite.backend.infrastructure.persistence.entity.NotificationEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersistenceNotificationMapper {
    PersistenceNotificationMapper INSTANCE = Mappers.getMapper(PersistenceNotificationMapper.class);
    
    @Mapping(target = "userId", source = "user.id")
    Notification toDomain(NotificationEntity entity);
    
    @Mapping(target = "user", ignore = true)
    NotificationEntity toEntity(Notification domain);
    
    default NotificationEntity toEntity(Notification domain, UserEntity user) {
        NotificationEntity entity = toEntity(domain);
        entity.setUser(user);
        return entity;
    }
}
