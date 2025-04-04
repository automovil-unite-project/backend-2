package com.automovil_unite.backend.domain.repository;

import com.automovil_unite.backend.domain.model.Document;
import com.automovil_unite.backend.domain.model.DocumentType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository {
    Document save(Document document);
    Optional<Document> findById(UUID id);
    List<Document> findByUserId(UUID userId);
    Optional<Document> findByUserIdAndType(UUID userId, DocumentType type);
    void delete(UUID id);
}
