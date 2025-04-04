package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.model.Notification;
import com.automovil_unite.backend.domain.model.NotificationType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationService {
    Notification createNotification(Notification notification);
    Optional<Notification> findById(UUID id);
    List<Notification> findByUserId(UUID userId);
    List<Notification> findByUserIdAndRead(UUID userId, boolean read);
    List<Notification> findByUserIdAndType(UUID userId, NotificationType type);
    int countUnreadByUserId(UUID userId);
    void markAsRead(UUID id);
    void deleteNotification(UUID id);
    void sendRentalNotification(UUID rentalId, NotificationType type);
    void sendUserNotification(UUID userId, String title, String message, NotificationType type);
}