package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.model.Document;
import com.automovil_unite.backend.domain.model.DocumentType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentService {
    Document uploadDocument(Document document);
    Optional<Document> findById(UUID id);
    List<Document> findByUserId(UUID userId);
    Optional<Document> findByUserIdAndType(UUID userId, DocumentType type);
    Document updateDocument(Document document);
    void deleteDocument(UUID id);
    boolean verifyDocument(UUID documentId, boolean approved);
}