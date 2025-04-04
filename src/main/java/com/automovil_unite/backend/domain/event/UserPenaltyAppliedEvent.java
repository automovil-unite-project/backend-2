package com.automovil_unite.backend.domain.event;

import com.automovil_unite.backend.domain.model.PenaltyType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserPenaltyAppliedEvent extends DomainEvent {
    private final UUID userId;
    private final UUID rentalId;
    private final PenaltyType penaltyType;

    public UserPenaltyAppliedEvent(UUID userId, UUID rentalId, PenaltyType penaltyType) {
        super();
        this.userId = userId;
        this.rentalId = rentalId;
        this.penaltyType = penaltyType;
    }
}
