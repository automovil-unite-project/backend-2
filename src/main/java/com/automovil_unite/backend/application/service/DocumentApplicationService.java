package com.automovil_unite.backend.application.service;

import com.automovil_unite.backend.application.dto.DocumentDto;
import com.automovil_unite.backend.application.mapper.DocumentMapper;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.exception.UnauthorizedActionException;
import com.automovil_unite.backend.domain.model.Document;
import com.automovil_unite.backend.domain.model.DocumentType;
import com.automovil_unite.backend.domain.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentApplicationService {
    private final DocumentService documentService;
    private final DocumentMapper documentMapper;
    private final String uploadDir = "./uploads/documents";

    @Transactional
    public DocumentDto uploadDocument(UUID userId, DocumentType type, MultipartFile file) throws IOException {
        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generar nombre único para archivo
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        
        // Guardar archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Crear entidad de documento
        Document document = Document.builder()
                .userId(userId)
                .type(type)
                .filename(fileName)
                .contentType(file.getContentType())
                .path(filePath.toString())
                .verified(false)
                .uploadedAt(LocalDateTime.now())
                .build();
        
        Document savedDocument = documentService.uploadDocument(document);
        return documentMapper.toDto(savedDocument);
    }

    @Transactional(readOnly = true)
    public DocumentDto getDocumentById(UUID id) {
        Document document = documentService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document", id.toString()));
        return documentMapper.toDto(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentDto> getDocumentsByUserId(UUID userId) {
        List<Document> documents = documentService.findByUserId(userId);
        return documents.stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentDto getDocumentByUserIdAndType(UUID userId, DocumentType type) {
        Document document = documentService.findByUserIdAndType(userId, type)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Document not found for user " + userId + " and type " + type));
        return documentMapper.toDto(document);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public DocumentDto verifyDocument(UUID documentId, boolean approved) {
        // Primero obtenemos el documento
        Document document = documentService.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document", documentId.toString()));

        // Luego llamamos al método verifyDocument que devuelve un boolean
        boolean verified = documentService.verifyDocument(documentId, approved);

        // Si la verificación fue exitosa, podemos buscar el documento actualizado
        if (verified) {
            document = documentService.findById(documentId)
                    .orElseThrow(() -> new EntityNotFoundException("Document", documentId.toString()));
        }

        return documentMapper.toDto(document);
    }

    @Transactional
    public void deleteDocument(UUID id, UUID userId) {
        Document document = documentService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document", id.toString()));
        
        // Solo el propietario puede eliminar su documento
        if (!document.getUserId().equals(userId)) {
            throw new UnauthorizedActionException(userId, "document", "delete");
        }
        
        try {
            // Eliminar archivo físico
            Files.deleteIfExists(Paths.get(document.getPath()));
            
            // Eliminar registro de base de datos
            documentService.deleteDocument(id);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete document file", e);
        }
    }
}