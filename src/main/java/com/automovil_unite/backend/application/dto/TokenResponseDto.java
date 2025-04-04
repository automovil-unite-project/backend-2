package com.automovil_unite.backend.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {
    private String token;
    private String tokenType;
    private long expiresIn;
    private boolean requiresTwoFactor;
}
