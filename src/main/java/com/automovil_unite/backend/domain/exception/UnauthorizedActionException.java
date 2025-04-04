package com.automovil_unite.backend.domain.exception;

import java.util.UUID;

public class UnauthorizedActionException extends DomainException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
    
    public UnauthorizedActionException(UUID userId, String resource, String action) {
        super(String.format("User %s is not authorized to %s %s", userId, action, resource));
    }
}