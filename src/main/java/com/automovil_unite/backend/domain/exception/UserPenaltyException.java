package com.automovil_unite.backend.domain.exception;

import java.util.UUID;

public class UserPenaltyException extends DomainException {
    public UserPenaltyException(UUID userId) {
        super(String.format("User %s has active penalties and cannot perform this action", userId));
    }
}