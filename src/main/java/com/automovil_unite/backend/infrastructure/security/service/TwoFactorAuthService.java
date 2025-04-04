package com.automovil_unite.backend.infrastructure.security.service;

import com.automovil_unite.backend.domain.model.VerificationCode;
import com.automovil_unite.backend.domain.model.VerificationCodeType;
import com.automovil_unite.backend.domain.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthService {
    private final VerificationService verificationService;
    private final SecureRandom random = new SecureRandom();

    public VerificationCode generateTfaCode(UUID userId, VerificationCodeType type) {
        return verificationService.generateCode(userId, type);
    }

    public boolean verifyTfaCode(UUID userId, String code, VerificationCodeType type) {
        return verificationService.verifyCode(userId, code, type);
    }
}
