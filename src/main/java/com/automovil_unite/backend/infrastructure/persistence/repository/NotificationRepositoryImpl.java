package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.Notification;
import com.automovil_unite.backend.domain.model.NotificationType;
import com.automovil_unite.backend.domain.repository.NotificationRepository;
import com.automovil_unite.backend.infrastructure.persistence.entity.NotificationEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.mapper.PersistenceNotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {
    private final JpaNotificationRepository jpaNotificationRepository;
    private final JpaUserRepository jpaUserRepository;
    private final PersistenceNotificationMapper notificationMapper = PersistenceNotificationMapper.INSTANCE;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity;
        
        UserEntity user = jpaUserRepository.findById(notification.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + notification.getUserId()));
        
        if (notification.getId() != null) {
            entity = jpaNotificationRepository.findById(notification.getId())
                    .orElse(new NotificationEntity());
            entity = notificationMapper.toEntity(notification, user);
        } else {
            entity = notificationMapper.toEntity(notification, user);
        }
        return notificationMapper.toDomain(jpaNotificationRepository.save(entity));
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaNotificationRepository.findById(id)
                .map(notificationMapper::toDomain);
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        UserEntity user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        return jpaNotificationRepository.findByUser(user).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndRead(UUID userId, boolean read) {
        UserEntity user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Cambia esta línea para usar findByUserAndIsRead en lugar de findByUserAndRead
        return jpaNotificationRepository.findByUserAndIsRead(user, read).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndType(UUID userId, NotificationType type) {
        UserEntity user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return jpaNotificationRepository.findByUserAndType(user, type).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int countUnreadByUserId(UUID userId) {
        return jpaNotificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(UUID id) {
        jpaNotificationRepository.markAsRead(id, LocalDateTime.now());
    }

    @Override
    public void delete(UUID id) {
        jpaNotificationRepository.deleteById(id);
    }
}
