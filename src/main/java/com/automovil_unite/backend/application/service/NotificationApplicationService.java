package com.automovil_unite.backend.application.service;

import com.automovil_unite.backend.application.dto.NotificationDto;
import com.automovil_unite.backend.application.mapper.NotificationMapper;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.exception.UnauthorizedActionException;
import com.automovil_unite.backend.domain.model.Notification;
import com.automovil_unite.backend.domain.model.NotificationType;
import com.automovil_unite.backend.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationApplicationService {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByUserId(UUID userId) {
        List<Notification> notifications = notificationService.findByUserId(userId);
        return notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(UUID userId) {
        List<Notification> notifications = notificationService.findByUserIdAndRead(userId, false);
        return notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int countUnreadByUserId(UUID userId) {
        return notificationService.countUnreadByUserId(userId);
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationService.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification", notificationId.toString()));
        
        // Verificar que el usuario sea el propietario de la notificación
        if (!notification.getUserId().equals(userId)) {
            throw new UnauthorizedActionException(userId, "notification", "mark as read");
        }
        
        notificationService.markAsRead(notificationId);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> unreadNotifications = notificationService.findByUserIdAndRead(userId, false);
        unreadNotifications.forEach(notification -> notificationService.markAsRead(notification.getId()));
    }

    @Transactional
    public void deleteNotification(UUID notificationId, UUID userId) {
        Notification notification = notificationService.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification", notificationId.toString()));
        
        // Verificar que el usuario sea el propietario de la notificación
        if (!notification.getUserId().equals(userId)) {
            throw new UnauthorizedActionException(userId, "notification", "delete");
        }
        
        notificationService.deleteNotification(notificationId);
    }

    @Transactional
    public void sendRentalNotification(UUID rentalId, NotificationType type) {
        notificationService.sendRentalNotification(rentalId, type);
    }

    @Transactional
    public void sendUserNotification(UUID userId, String title, String message, NotificationType type) {
        notificationService.sendUserNotification(userId, title, message, type);
    }
}