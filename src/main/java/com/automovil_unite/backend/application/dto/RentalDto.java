package com.automovil_unite.backend.application.dto;

import com.automovil_unite.backend.domain.model.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalDto {
    private UUID id;
    private UUID vehicleId;
    private VehicleDto vehicle;
    private UUID tenantId;
    private String tenantName;
    private UUID ownerId;
    private String ownerName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime actualEndDate;
    private String verificationCode;
    private boolean verificationCodeConfirmed;
    private BigDecimal originalPrice;
    private BigDecimal counterOfferPrice;
    private BigDecimal finalPrice;
    private RentalStatus status;
    private boolean discountApplied;
    private boolean penaltyApplied;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
