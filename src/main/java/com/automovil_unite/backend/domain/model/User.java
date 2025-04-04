package com.automovil_unite.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String identificationNumber; // Número de DNI
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
    
    @Builder.Default
    private Set<Document> documents = new HashSet<>();
    
    public boolean hasFuturePenalty() {
        return reportCount > 0;
    }
    
    public boolean isEligibleForDiscount() {
        return rating >= 4.7 && reportCount == 0 && role == Role.TENANT;
    }
    
    public boolean hasFullDocumentation() {
        return identificationVerified && criminalRecordVerified && 
               (role == Role.TENANT ? drivingLicenseVerified : true) && 
               profilePhotoVerified;
    }
}