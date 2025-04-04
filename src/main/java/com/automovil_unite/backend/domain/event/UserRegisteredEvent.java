package com.automovil_unite.backend.domain.event;

import com.automovil_unite.backend.domain.model.User;
import lombok.Getter;

@Getter
public class UserRegisteredEvent extends DomainEvent {
    private final User user;

    public UserRegisteredEvent(User user) {
        super();
        this.user = user;
    }
}
