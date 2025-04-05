package com.automovil_unite.backend.presentation.controller;

import com.automovil_unite.backend.application.dto.DocumentDto;
import com.automovil_unite.backend.application.service.DocumentApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Documentos", description = "Administración de documentos (para administradores)")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('ADMIN')")
public class DocumentController {

    private final DocumentApplicationService documentService;

    @Autowired
    public DocumentController(DocumentApplicationService documentService) {
        this.documentService = documentService;
    }

//    @GetMapping
//    @Operation(summary = "Obtener todos los documentos", description = "Recupera todos los documentos en el sistema (solo administradores)")
//    public ResponseEntity<List<DocumentDto>> getAllDocuments() {
//        List<DocumentDto> documents = documentService.getAllDocuments();
//        return ResponseEntity.ok(documents);
//    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener documento por ID", description = "Recupera un documento específico por su ID (solo administradores)")
    public ResponseEntity<DocumentDto> getDocumentById(@PathVariable UUID id) {
        DocumentDto document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener documentos de un usuario", description = "Recupera todos los documentos de un usuario específico (solo administradores)")
    public ResponseEntity<List<DocumentDto>> getDocumentsByUserId(@PathVariable UUID userId) {
        List<DocumentDto> documents = documentService.getDocumentsByUserId(userId);
        return ResponseEntity.ok(documents);
    }

//    @GetMapping("/pending-verification")
//    @Operation(summary = "Obtener documentos pendientes de verificación", description = "Recupera los documentos que no han sido verificados (solo administradores)")
//    public ResponseEntity<List<DocumentDto>> getPendingVerificationDocuments() {
//        List<DocumentDto> documents = documentService.getPendingVerificationDocuments();
//        return ResponseEntity.ok(documents);
//    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "Verificar documento", description = "Aprueba o rechaza un documento (solo administradores)")
    public ResponseEntity<DocumentDto> verifyDocument(
            @PathVariable UUID id,
            @RequestParam boolean approved) {
        
        DocumentDto document = documentService.verifyDocument(id, approved);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar documento", description = "Elimina un documento del sistema (solo administradores)")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        //documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    // Este endpoint es para descargar el archivo físico
    @GetMapping("/{id}/download")
    @Operation(summary = "Descargar documento", description = "Descarga el archivo físico de un documento (solo administradores)")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable UUID id) {
        // Esta implementación debe ser completada con el manejo real de archivos
        // Debería devolver el archivo con sus headers correspondientes
        return ResponseEntity.ok().build();
    }
}