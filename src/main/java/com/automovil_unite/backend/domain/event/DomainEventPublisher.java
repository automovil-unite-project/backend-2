package com.automovil_unite.backend.domain.event;

import org.springframework.stereotype.Service;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
