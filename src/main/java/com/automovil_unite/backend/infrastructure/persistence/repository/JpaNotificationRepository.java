package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.NotificationType;
import com.automovil_unite.backend.infrastructure.persistence.entity.NotificationEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByUser(UserEntity user);

    // Cambia este método para usar isRead en lugar de read
    List<NotificationEntity> findByUserAndIsRead(UserEntity user, boolean isRead);

    List<NotificationEntity> findByUserAndType(UserEntity user, NotificationType type);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.user.id = :userId AND n.isRead = false")
    int countUnreadByUserId(UUID userId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :now WHERE n.id = :id")
    void markAsRead(UUID id, LocalDateTime now);
}