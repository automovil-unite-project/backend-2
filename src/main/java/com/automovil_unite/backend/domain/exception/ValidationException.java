package com.automovil_unite.backend.domain.exception;

public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}