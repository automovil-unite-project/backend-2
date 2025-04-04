package com.automovil_unite.backend.application.dto;

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
public class VehiclePhotoDto {
    private UUID id;
    private UUID vehicleId;
    private String filename;
    private String contentType;
    private String path;
    private boolean main;
    private LocalDateTime uploadedAt;
}
