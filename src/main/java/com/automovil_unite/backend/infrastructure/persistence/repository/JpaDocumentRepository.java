package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.DocumentType;
import com.automovil_unite.backend.infrastructure.persistence.entity.DocumentEntity;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaDocumentRepository extends JpaRepository<DocumentEntity, UUID> {
    List<DocumentEntity> findByUser(UserEntity user);
    
    Optional<DocumentEntity> findByUserAndType(UserEntity user, DocumentType type);
}
