package com.automovil_unite.backend.domain.event;

import com.automovil_unite.backend.domain.model.Rental;
import com.automovil_unite.backend.domain.model.RentalStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RentalStatusChangedEvent extends DomainEvent {
    private final UUID rentalId;
    private final RentalStatus oldStatus;
    private final RentalStatus newStatus;

    public RentalStatusChangedEvent(UUID rentalId, RentalStatus oldStatus, RentalStatus newStatus) {
        super();
        this.rentalId = rentalId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
