package com.automovil_unite.backend.domain.model;

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
public class Notification {
    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private NotificationType type;
    private String referenceId; // ID relacionado (rental, vehicle, etc.)
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}