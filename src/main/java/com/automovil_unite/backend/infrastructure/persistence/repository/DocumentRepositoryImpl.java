package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.Document;
import com.automovil_unite.backend.domain.model.DocumentType;
import com.automovil_unite.backend.domain.repository.DocumentRepository;
import com.automovil_unite.backend.infrastructure.persistence.entity.DocumentEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.mapper.PersistenceDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {
    
    private final JpaDocumentRepository jpaDocumentRepository;
    private final JpaUserRepository jpaUserRepository;
    private final PersistenceDocumentMapper documentMapper = PersistenceDocumentMapper.INSTANCE;
    


    @Override
    public Document save(Document document) {
        DocumentEntity entity;
        
        // Obtener el usuario por ID
        UserEntity user = jpaUserRepository.findById(document.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + document.getUserId()));
        
        if (document.getId() != null) {
            entity = jpaDocumentRepository.findById(document.getId())
                    .orElse(new DocumentEntity());
            entity = documentMapper.toEntity(document, user);
        } else {
            entity = documentMapper.toEntity(document, user);
        }
        
        return documentMapper.toDomain(jpaDocumentRepository.save(entity));
    }

    @Override
    public Optional<Document> findById(UUID id) {
        return jpaDocumentRepository.findById(id)
                .map(documentMapper::toDomain);
    }

    @Override
    public List<Document> findByUserId(UUID userId) {
        UserEntity user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        return jpaDocumentRepository.findByUser(user).stream()
                .map(documentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Document> findByUserIdAndType(UUID userId, DocumentType type) {
        UserEntity user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        return jpaDocumentRepository.findByUserAndType(user, type)
                .map(documentMapper::toDomain);
    }

    @Override
    public void delete(UUID id) {
        jpaDocumentRepository.deleteById(id);
    }
}