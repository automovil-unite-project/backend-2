package com.automovil_unite.backend.domain.exception;

import java.util.UUID;

public class RentalExtensionException extends DomainException {
    public RentalExtensionException(UUID rentalId, String reason) {
        super(String.format("Cannot extend rental %s: %s", rentalId, reason));
    }
}