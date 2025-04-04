package com.automovil_unite.backend.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalCounterOfferDto {
    @NotNull(message = "Counter offer price is required")
    @Positive(message = "Counter offer price must be positive")
    private BigDecimal counterOfferPrice;
}
