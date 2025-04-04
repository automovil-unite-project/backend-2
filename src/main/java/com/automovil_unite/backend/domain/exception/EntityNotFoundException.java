package com.automovil_unite.backend.domain.exception;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String entityName, String id) {
        super(String.format("%s with id %s not found", entityName, id));
    }
}
