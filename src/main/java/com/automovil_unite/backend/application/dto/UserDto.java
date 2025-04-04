package com.automovil_unite.backend.application.dto;

import com.automovil_unite.backend.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String identificationNumber;
    private boolean identificationVerified;
    private boolean criminalRecordVerified;
    private boolean drivingLicenseVerified;
    private boolean profilePhotoVerified;
    private Role role;
    private double rating;
    private int reportCount;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<DocumentDto> documents;
}
