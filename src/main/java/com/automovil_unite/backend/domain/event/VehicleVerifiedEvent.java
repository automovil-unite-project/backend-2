package com.automovil_unite.backend.domain.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class VehicleVerifiedEvent extends DomainEvent {
    private final UUID vehicleId;
    private final boolean approved;

    public VehicleVerifiedEvent(UUID vehicleId, boolean approved) {
        super();
        this.vehicleId = vehicleId;
        this.approved = approved;
    }
}