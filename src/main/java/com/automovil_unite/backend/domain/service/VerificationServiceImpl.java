package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.model.User;
import com.automovil_unite.backend.domain.model.VerificationCode;
import com.automovil_unite.backend.domain.model.VerificationCodeType;
import com.automovil_unite.backend.domain.repository.UserRepository;
import com.automovil_unite.backend.domain.service.VerificationService;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.VerificationCodeEntity;
import com.automovil_unite.backend.infrastructure.persistence.mapper.PersistenceVerificationCodeMapper;
import com.automovil_unite.backend.infrastructure.persistence.repository.JpaUserRepository;
import com.automovil_unite.backend.infrastructure.persistence.repository.JpaVerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {
    
    private final JpaVerificationCodeRepository verificationCodeRepository;
    private final JpaUserRepository userRepository;
    private final UserRepository domainUserRepository;
    private final PersistenceVerificationCodeMapper verificationCodeMapper = PersistenceVerificationCodeMapper.INSTANCE;
    private final JavaMailSender mailSender;
    
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRATION_MINUTES = 15;
    private static final SecureRandom random = new SecureRandom();



    @Override
    @Transactional
    public VerificationCode generateCode(UUID userId, VerificationCodeType type) {
        // Verificar que el usuario existe
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Invalidar códigos existentes
        verificationCodeRepository.findByUserAndTypeAndUsedFalseAndExpiresAtAfter(
                user, type, LocalDateTime.now())
                .ifPresent(code -> {
                    code.setUsed(true);
                    code.setUsedAt(LocalDateTime.now());
                    verificationCodeRepository.save(code);
                });
        
        // Generar nuevo código
        String code = generateRandomCode(CODE_LENGTH);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);
        
        VerificationCodeEntity entity = VerificationCodeEntity.builder()
                .user(user)
                .code(code)
                .type(type)
                .used(false)
                .expiresAt(expiresAt)
                .build();
        
        VerificationCodeEntity savedEntity = verificationCodeRepository.save(entity);
        
        // Enviar el código por email
        sendVerificationEmail(userId, code, type);
        
        return verificationCodeMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VerificationCode> findByUserIdAndType(UUID userId, VerificationCodeType type) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        return verificationCodeRepository.findByUserAndTypeAndUsedFalseAndExpiresAtAfter(
                user, type, LocalDateTime.now())
                .map(verificationCodeMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VerificationCode> findByCode(String code) {
        return verificationCodeRepository.findByCodeAndTypeAndUsedFalseAndExpiresAtAfter(
                code, VerificationCodeType.RENTAL_CONFIRM, LocalDateTime.now())
                .map(verificationCodeMapper::toDomain);
    }

    @Override
    @Transactional
    public boolean verifyCode(UUID userId, String code, VerificationCodeType type) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        Optional<VerificationCodeEntity> verificationCodeOpt = 
                verificationCodeRepository.findByUserAndTypeAndUsedFalseAndExpiresAtAfter(
                user, type, LocalDateTime.now());
        
        if (verificationCodeOpt.isPresent() && verificationCodeOpt.get().getCode().equals(code)) {
            VerificationCodeEntity codeEntity = verificationCodeOpt.get();
            codeEntity.setUsed(true);
            codeEntity.setUsedAt(LocalDateTime.now());
            verificationCodeRepository.save(codeEntity);
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional
    public void invalidateCode(UUID codeId) {
        verificationCodeRepository.markAsUsed(codeId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteExpiredCodes() {
        verificationCodeRepository.deleteExpiredCodes(LocalDateTime.now());
    }

    @Override
    public boolean sendVerificationEmail(UUID userId, String code, VerificationCodeType type) {
        try {
            Optional<User> userOpt = domainUserRepository.findById(userId);
            
            if (!userOpt.isPresent()) {
                return false;
            }
            
            User user = userOpt.get();
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            
            switch (type) {
                case REGISTRATION:
                    message.setSubject("Verificación de registro en Car Rental App");
                    message.setText("Gracias por registrarte en Car Rental App. Tu código de verificación es: " + code);
                    break;
                case PASSWORD_RESET:
                    message.setSubject("Restablecer contraseña en Car Rental App");
                    message.setText("Tu código para restablecer la contraseña es: " + code);
                    break;
                case LOGIN:
                    message.setSubject("Código de autenticación para Car Rental App");
                    message.setText("Tu código de autenticación para iniciar sesión es: " + code);
                    break;
                case RENTAL_CONFIRM:
                    message.setSubject("Confirmación de alquiler en Car Rental App");
                    message.setText("El código para confirmar la entrega del vehículo es: " + code);
                    break;
            }
            
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}