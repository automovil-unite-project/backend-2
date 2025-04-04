package com.automovil_unite.backend.presentation.controller;

import com.automovil_unite.backend.application.dto.*;
import com.automovil_unite.backend.application.service.UserApplicationService;
import com.automovil_unite.backend.domain.model.VerificationCodeType;
import com.automovil_unite.backend.domain.service.SecurityService;
import com.automovil_unite.backend.domain.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para autenticación y gestión de usuarios")
public class AuthController {
    private final UserApplicationService userService;
    private final SecurityService securityService;
    private final VerificationService verificationService;
    private final AuthenticationManager authenticationManager;

    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Registra un nuevo usuario en el sistema (arrendatario o dueño) y envía un código de verificación por email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente", 
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos", 
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Email ya registrado", 
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(
            @Parameter(description = "Datos de registro del usuario", required = true)
            @Valid @RequestBody UserRegistrationDto registrationDto) {
        UserDto createdUser = userService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario y devuelve un token JWT. Si está habilitada la autenticación de dos factores, se enviará un código al email del usuario"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa", 
                    content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", 
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
            @Parameter(description = "Credenciales de inicio de sesión", required = true)
            @Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Obtener el ID de usuario
            UUID userId = UUID.fromString(authentication.getName());
            
            // Verificar si se requiere autenticación de dos factores
            boolean requiresTwoFactor = securityService.requiresTwoFactor(userId);
            
            // Si se requiere 2FA, generar código y enviarlo por correo
            if (requiresTwoFactor) {
                verificationService.generateCode(userId, VerificationCodeType.LOGIN);
            }
            
            // Generar token JWT
            String token = securityService.generateToken(userId);
            
            TokenResponseDto response = TokenResponseDto.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .expiresIn(3600) // 1 hora
                    .requiresTwoFactor(requiresTwoFactor)
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(
            summary = "Verificar código de dos factores",
            description = "Verifica el código de autenticación de dos factores y devuelve un token JWT completo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación exitosa", 
                    content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Código inválido", 
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/verify-2fa")
    public ResponseEntity<TokenResponseDto> verifyTwoFactor(
            @Parameter(description = "Código de verificación", required = true)
            @Valid @RequestBody TwoFactorVerificationDto verificationDto) {
        // Obtener el ID de usuario del token (se asume que ya tiene un token temporal)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        // Verificar el código
        boolean verified = securityService.validateTwoFactor(userId, verificationDto.getCode());
        
        if (verified) {
            // Generar un nuevo token sin restricción de 2FA
            String token = securityService.generateToken(userId);
            
            TokenResponseDto response = TokenResponseDto.builder()
                    .token(token)
                    .expiresIn(3600) // 1 hora
                    .requiresTwoFactor(false)
                    .build();
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(
            summary = "Solicitar restablecimiento de contraseña",
            description = "Envía un código de verificación al email del usuario para restablecer su contraseña"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud procesada correctamente"),
            @ApiResponse(responseCode = "400", description = "Email inválido", 
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/reset-password-request")
    public ResponseEntity<Void> requestPasswordReset(
            @Parameter(description = "Email del usuario", required = true)
            @Valid @RequestBody PasswordResetRequestDto request) {
        try {
            UserDto user = userService.getUserByEmail(request.getEmail());
            verificationService.generateCode(user.getId(), VerificationCodeType.PASSWORD_RESET);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Por seguridad, no revelar si el correo existe o no
            return ResponseEntity.ok().build();
        }
    }

    @Operation(
            summary = "Restablecer contraseña",
            description = "Restablece la contraseña del usuario utilizando el código de verificación enviado previamente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña restablecida correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o código incorrecto", 
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "Datos para restablecer contraseña", required = true)
            @Valid @RequestBody PasswordResetDto resetDto) {
        // Esta implementación es simplificada - en producción se debería verificar el código
        return ResponseEntity.ok().build();
    }
}