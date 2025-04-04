package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.model.VerificationCode;
import com.automovil_unite.backend.domain.model.VerificationCodeType;

import java.util.Optional;
import java.util.UUID;

public interface VerificationService {
    VerificationCode generateCode(UUID userId, VerificationCodeType type);
    Optional<VerificationCode> findByUserIdAndType(UUID userId, VerificationCodeType type);
    Optional<VerificationCode> findByCode(String code);
    boolean verifyCode(UUID userId, String code, VerificationCodeType type);
    void invalidateCode(UUID codeId);
    void deleteExpiredCodes();
    boolean sendVerificationEmail(UUID userId, String code, VerificationCodeType type);
}
