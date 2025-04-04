package com.automovil_unite.backend.domain.event;

import com.automovil_unite.backend.domain.model.Review;
import lombok.Getter;

@Getter
public class ReviewCreatedEvent extends DomainEvent {
    private final Review review;

    public ReviewCreatedEvent(Review review) {
        super();
        this.review = review;
    }
}
