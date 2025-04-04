package com.automovil_unite.backend.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    @Email(message = "Email should be valid")
    private String email;
    
    private String firstName;
    private String lastName;
    private String phoneNumber;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
