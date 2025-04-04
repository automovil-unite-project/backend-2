package com.automovil_unite.backend.domain.service;

import java.util.UUID;

public interface SecurityService {
    String encodePassword(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
    String generateToken(UUID userId);
    UUID validateToken(String token);
    boolean requiresTwoFactor(UUID userId);
    boolean validateTwoFactor(UUID userId, String code);
}