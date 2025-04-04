package com.automovil_unite.backend.domain.exception;

import java.time.LocalDateTime;
import java.util.UUID;

public class VehicleNotAvailableException extends DomainException {
    public VehicleNotAvailableException(UUID vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        super(String.format("Vehicle with id %s is not available from %s to %s", 
                vehicleId, startDate, endDate));
    }
}