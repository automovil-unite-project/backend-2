package com.automovil_unite.backend.domain.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RentalLateEvent extends DomainEvent {
    private final UUID rentalId;
    private final UUID tenantId;
    private final UUID vehicleId;

    public RentalLateEvent(UUID rentalId, UUID tenantId, UUID vehicleId) {
        super();
        this.rentalId = rentalId;
        this.tenantId = tenantId;
        this.vehicleId = vehicleId;
    }
}
