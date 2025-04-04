package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.event.DomainEventPublisher;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.model.Document;
import com.automovil_unite.backend.domain.model.DocumentType;
import com.automovil_unite.backend.domain.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {
    
    private final DocumentRepository documentRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final DomainEventPublisher eventPublisher;
    
    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            UserService userService,
            NotificationService notificationService,
            DomainEventPublisher eventPublisher) {
        this.documentRepository = documentRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Document uploadDocument(Document document) {
        // Verificar que el usuario existe
        userService.findById(document.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", document.getUserId().toString()));
        
        // Verificar si ya existe un documento del mismo tipo para este usuario
        Optional<Document> existingDoc = documentRepository.findByUserIdAndType(document.getUserId(), document.getType());
        
        if (existingDoc.isPresent()) {
            // Actualizar el documento existente
            Document existing = existingDoc.get();
            existing.setFilename(document.getFilename());
            existing.setContentType(document.getContentType());
            existing.setPath(document.getPath());
            existing.setVerified(false); // Requiere nueva verificación
            existing.setUploadedAt(LocalDateTime.now());
            existing.setVerifiedAt(null);
            
            return documentRepository.save(existing);
        } else {
            // Crear nuevo documento
            return documentRepository.save(document);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Document> findById(UUID id) {
        return documentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> findByUserId(UUID userId) {
        return documentRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Document> findByUserIdAndType(UUID userId, DocumentType type) {
        return documentRepository.findByUserIdAndType(userId, type);
    }

    @Override
    @Transactional
    public Document updateDocument(Document document) {
        // Verificar que el documento existe
        documentRepository.findById(document.getId())
                .orElseThrow(() -> new EntityNotFoundException("Document", document.getId().toString()));
        
        return documentRepository.save(document);
    }

    @Override
    @Transactional
    public void deleteDocument(UUID id) {
        documentRepository.delete(id);
    }

    @Override
    @Transactional
    public boolean verifyDocument(UUID documentId, boolean approved) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document", documentId.toString()));
        
        document.setVerified(approved);
        document.setVerifiedAt(LocalDateTime.now());
        documentRepository.save(document);
        
        // Actualizar el estado de verificación del usuario
        userService.verifyUserDocument(document.getUserId(), documentId, approved);
        
        // Notificar al usuario
        notificationService.sendUserNotification(
                document.getUserId(),
                approved ? "Documento verificado" : "Documento rechazado",
                approved ? "Tu documento ha sido verificado correctamente." : "Tu documento ha sido rechazado. Por favor, sube un documento válido.",
                approved ? com.automovil_unite.backend.domain.model.NotificationType.DOCUMENT_VERIFIED : 
                           com.automovil_unite.backend.domain.model.NotificationType.DOCUMENT_REJECTED
        );
        
        return approved;
    }
}