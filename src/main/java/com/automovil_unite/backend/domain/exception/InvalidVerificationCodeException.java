package com.automovil_unite.backend.domain.exception;

public class InvalidVerificationCodeException extends DomainException {
    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}
