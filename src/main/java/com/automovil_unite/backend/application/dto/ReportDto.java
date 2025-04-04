package com.automovil_unite.backend.application.dto;

import com.automovil_unite.backend.domain.model.ReportStatus;
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
public class ReportDto {
    private UUID id;
    private UUID rentalId;
    private UUID reporterId;
    private String reporterName;
    private UUID reportedUserId;
    private String reportedUserName;
    private String reason;
    private String description;
    private boolean resolved;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
