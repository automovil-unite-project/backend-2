package com.automovil_unite.backend.presentation.controller;

import com.automovil_unite.backend.application.dto.DocumentDto;
import com.automovil_unite.backend.application.dto.UserDto;
import com.automovil_unite.backend.application.dto.UserUpdateDto;

import com.automovil_unite.backend.application.service.DocumentApplicationService;
import com.automovil_unite.backend.application.service.UserApplicationService;
import com.automovil_unite.backend.domain.model.DocumentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios", description = "Gestión de usuarios y documentos personales")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

    private final UserApplicationService userService;
    private final DocumentApplicationService documentService;

    @Autowired
    public UserController(
            UserApplicationService userService,
            DocumentApplicationService documentService) {
        this.userService = userService;
        this.documentService = documentService;
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener perfil del usuario actual", description = "Recupera la información del usuario autenticado")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar perfil del usuario", description = "Actualiza la información personal del usuario autenticado")
    public ResponseEntity<UserDto> updateCurrentUser(@Valid @RequestBody UserUpdateDto updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        UserDto updatedUser = userService.updateUser(userId, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Recupera la información de un usuario específico por su ID")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #id")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Recupera la lista de todos los usuarios (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping(value = "/me/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir documento", description = "Sube un documento personal (DNI, antecedentes, licencia, etc.)")
    public ResponseEntity<DocumentDto> uploadDocument(
            @RequestParam("type") DocumentType documentType,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        // Esta implementación debe ser completada con el manejo real de archivos
        DocumentDto document = documentService.uploadDocument(userId, documentType, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @GetMapping("/me/documents")
    @Operation(summary = "Obtener documentos del usuario", description = "Recupera todos los documentos subidos por el usuario actual")
    public ResponseEntity<List<DocumentDto>> getUserDocuments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        List<DocumentDto> documents = documentService.getDocumentsByUserId(userId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/me/documents/{documentId}")
    @Operation(summary = "Obtener documento específico", description = "Recupera un documento específico del usuario actual")
    public ResponseEntity<DocumentDto> getUserDocument(@PathVariable UUID documentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        DocumentDto document = documentService.getDocumentById(documentId);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/me/documents/{documentId}")
    @Operation(summary = "Eliminar documento", description = "Elimina un documento del usuario actual")
    public ResponseEntity<Void> deleteUserDocument(@PathVariable UUID documentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
        
        documentService.deleteDocument(documentId, userId);
        return ResponseEntity.noContent().build();
    }
}