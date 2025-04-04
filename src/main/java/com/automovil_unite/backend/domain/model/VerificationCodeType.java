package com.automovil_unite.backend.domain.model;

public enum VerificationCodeType {
    REGISTRATION,     // Registro de usuario
    PASSWORD_RESET,   // Restablecimiento de contraseña
    LOGIN,            // Inicio de sesión (2FA)
    RENTAL_CONFIRM    // Confirmación de alquiler
}