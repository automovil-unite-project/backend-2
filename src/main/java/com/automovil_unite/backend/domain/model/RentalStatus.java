package com.automovil_unite.backend.domain.model;

public enum RentalStatus {
    PENDING,       // Solicitud inicial
    COUNTER_OFFER, // Contraoferta realizada por el arrendatario
    ACCEPTED,      // Aceptado por el dueño
    REJECTED,      // Rechazado por el dueño
    CONFIRMED,     // Confirmado con código de verificación (entrega del vehículo)
    IN_PROGRESS,   // En progreso (el vehículo está siendo utilizado)
    COMPLETED,     // Completado (el vehículo ha sido devuelto)
    CANCELLED,     // Cancelado antes de la entrega
    EXTENDED       // Extendido a un período más largo
}