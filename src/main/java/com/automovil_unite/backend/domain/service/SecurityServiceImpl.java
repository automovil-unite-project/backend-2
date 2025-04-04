package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.model.VerificationCodeType;

import com.automovil_unite.backend.infrastructure.config.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityServiceImpl implements SecurityService {
    
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final VerificationService verificationService;
    
    @Autowired
    public SecurityServiceImpl(PasswordEncoder passwordEncoder, 
                              JwtTokenProvider tokenProvider,
                              VerificationService verificationService) {
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.verificationService = verificationService;
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String generateToken(UUID userId) {
        return tokenProvider.generateToken(userId);
    }

    @Override
    public UUID validateToken(String token) {
        if (tokenProvider.validateToken(token)) {
            return tokenProvider.getUserIdFromToken(token);
        }
        return null;
    }

    @Override
    public boolean requiresTwoFactor(UUID userId) {
        // En una implementación real, esto podría depender de la configuración del usuario
        // Para este ejemplo, siempre devolvemos true
        return true;
    }

    @Override
    public boolean validateTwoFactor(UUID userId, String code) {
        return verificationService.verifyCode(userId, code, VerificationCodeType.LOGIN);
    }
}