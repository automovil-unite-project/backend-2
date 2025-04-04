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
public class Report {
    private UUID id;
    private UUID rentalId;
    private UUID reporterId;
    private UUID reportedUserId;
    private String reason;
    private String description;
    private boolean resolved;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}