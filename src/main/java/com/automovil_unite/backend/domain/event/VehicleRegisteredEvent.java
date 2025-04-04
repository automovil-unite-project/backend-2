package com.automovil_unite.backend.domain.event;

import com.automovil_unite.backend.domain.model.Vehicle;
import lombok.Getter;

@Getter
public class VehicleRegisteredEvent extends DomainEvent {
    private final Vehicle vehicle;

    public VehicleRegisteredEvent(Vehicle vehicle) {
        super();
        this.vehicle = vehicle;
    }
}
