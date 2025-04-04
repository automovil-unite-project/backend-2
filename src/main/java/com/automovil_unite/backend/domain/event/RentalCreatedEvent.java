package com.automovil_unite.backend.domain.event;

import com.automovil_unite.backend.domain.model.Rental;
import lombok.Getter;

@Getter
public class RentalCreatedEvent extends DomainEvent {
    private final Rental rental;

    public RentalCreatedEvent(Rental rental) {
        super();
        this.rental = rental;
    }
}
