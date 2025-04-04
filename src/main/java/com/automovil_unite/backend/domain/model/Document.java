// Document.java
package com.automovil_unite.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    private UUID id;
    private UUID userId;
    private DocumentType type;
    private String filename;
    private String contentType;
    private String path;
    private boolean verified;
    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;
}