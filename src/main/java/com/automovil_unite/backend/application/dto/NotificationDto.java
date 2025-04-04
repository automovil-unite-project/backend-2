package com.automovil_unite.backend.application.dto;

import com.automovil_unite.backend.domain.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private NotificationType type;
    private String referenceId;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
