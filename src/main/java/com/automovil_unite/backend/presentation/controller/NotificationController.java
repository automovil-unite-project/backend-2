package com.automovil_unite.backend.presentation.controller;

import com.automovil_unite.backend.application.dto.NotificationDto;
import com.automovil_unite.backend.application.service.NotificationApplicationService;
import com.automovil_unite.backend.domain.model.NotificationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notificaciones", description = "Gestión de notificaciones del usuario")
@SecurityRequirement(name = "bearer-jwt")
public class NotificationController {

    private final NotificationApplicationService notificationService;

    @Autowired
    public NotificationController(NotificationApplicationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Obtener notificaciones", description = "Recupera todas las notificaciones del usuario actual")
    public ResponseEntity<List<NotificationDto>> getUserNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        List<NotificationDto> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    @Operation(summary = "Obtener notificaciones no leídas", description = "Recupera las notificaciones no leídas del usuario actual")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /*@GetMapping("/type/{type}")
    @Operation(summary = "Obtener notificaciones por tipo", description = "Recupera las notificaciones de un tipo específico del usuario actual")
    public ResponseEntity<List<NotificationDto>> getNotificationsByType(@PathVariable NotificationType type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        List<NotificationDto> notifications = notificationService.getNotificationsByType(userId, type);
        return ResponseEntity.ok(notifications);
    }*/

    @GetMapping("/count-unread")
    @Operation(summary = "Contar notificaciones no leídas", description = "Cuenta el número de notificaciones no leídas del usuario actual")
    public ResponseEntity<Integer> countUnreadNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        int count = notificationService.countUnreadByUserId(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/{id}/mark-read")
    @Operation(summary = "Marcar notificación como leída", description = "Marca una notificación específica como leída")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-all-read")
    @Operation(summary = "Marcar todas como leídas", description = "Marca todas las notificaciones del usuario actual como leídas")
    public ResponseEntity<Void> markAllNotificationsAsRead() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar notificación", description = "Elimina una notificación específica")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.noContent().build();
    }

    /*@DeleteMapping("/clear-all")
    @Operation(summary = "Eliminar todas las notificaciones", description = "Elimina todas las notificaciones del usuario actual")
    public ResponseEntity<Void> clearAllNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        notificationService.clearAllNotifications(userId);
        return ResponseEntity.noContent().build();
    }*/
}