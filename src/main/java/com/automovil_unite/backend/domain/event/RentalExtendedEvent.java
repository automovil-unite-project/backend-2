package com.automovil_unite.backend.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class RentalExtendedEvent extends DomainEvent {
    private final UUID rentalId;
    private final LocalDateTime oldEndDate;
    private final LocalDateTime newEndDate;

    public RentalExtendedEvent(UUID rentalId, LocalDateTime oldEndDate, LocalDateTime newEndDate) {
        super();
        this.rentalId = rentalId;
        this.oldEndDate = oldEndDate;
        this.newEndDate = newEndDate;
    }
}
