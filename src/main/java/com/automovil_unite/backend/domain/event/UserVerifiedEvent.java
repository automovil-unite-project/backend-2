package com.automovil_unite.backend.domain.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserVerifiedEvent extends DomainEvent {
    private final UUID userId;
    private final boolean fullyVerified;

    public UserVerifiedEvent(UUID userId, boolean fullyVerified) {
        super();
        this.userId = userId;
        this.fullyVerified = fullyVerified;
    }
}
