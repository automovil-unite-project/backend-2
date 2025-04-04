package com.automovil_unite.backend.domain.exception;

import com.automovil_unite.backend.domain.model.RentalStatus;
import java.util.UUID;

public class RentalStatusException extends DomainException {
    public RentalStatusException(UUID rentalId, RentalStatus currentStatus, String operation) {
        super(String.format("Cannot perform operation '%s' on rental %s with status %s", 
                operation, rentalId, currentStatus));
    }
}