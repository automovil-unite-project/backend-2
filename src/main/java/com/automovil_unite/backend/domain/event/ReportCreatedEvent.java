package com.automovil_unite.backend.domain.event;

import com.automovil_unite.backend.domain.model.Report;
import lombok.Getter;

@Getter
public class ReportCreatedEvent extends DomainEvent {
    private final Report report;

    public ReportCreatedEvent(Report report) {
        super();
        this.report = report;
    }
}
