package com.automovil_unite.backend.domain.exception;

import java.util.UUID;

public class ReviewException extends DomainException {
    public ReviewException(UUID userId, UUID rentalId, String reason) {
        super(String.format("User %s cannot review rental %s: %s", userId, rentalId, reason));
    }
}