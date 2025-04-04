package com.automovil_unite.backend.domain.exception;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String email) {
        super(String.format("User with email %s already exists", email));
    }
}