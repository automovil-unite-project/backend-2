package com.automovil_unite.backend.domain.model;

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
public class Rental {
    private UUID id;
    private UUID vehicleId;
    private UUID tenantId;
    private UUID ownerId;
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
    
    public boolean isActive() {
        return status == RentalStatus.ACCEPTED || 
               status == RentalStatus.CONFIRMED || 
               status == RentalStatus.IN_PROGRESS;
    }
    
    public boolean isLate() {
        LocalDateTime now = LocalDateTime.now();
        return isActive() && now.isAfter(endDate.plusMinutes(30));
    }
    
    public boolean canExtend() {
        // Solo se puede extender si está confirmado o en progreso y no está vencido
        return (status == RentalStatus.CONFIRMED || status == RentalStatus.IN_PROGRESS) && 
               !isLate();
    }
}