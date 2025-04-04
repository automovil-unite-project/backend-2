package com.automovil_unite.backend.domain.event;

import com.automovil_unite.backend.domain.model.VerificationCodeType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class VerificationCodeGeneratedEvent extends DomainEvent {
    private final UUID userId;
    private final String code;
    private final VerificationCodeType type;

    public VerificationCodeGeneratedEvent(UUID userId, String code, VerificationCodeType type) {
        super();
        this.userId = userId;
        this.code = code;
        this.type = type;
    }
}
