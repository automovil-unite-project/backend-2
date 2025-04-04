package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.model.User;
import com.automovil_unite.backend.domain.model.VerificationCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {
    User registerUser(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    User updateUser(User user);
    void deleteUser(UUID id);
    List<User> findAll();
    boolean existsByEmail(String email);
    boolean verifyUserDocument(UUID userId, UUID documentId, boolean approved);
    boolean applyPenalty(UUID userId, UUID rentalId);
    boolean isUserEligibleForRental(UUID userId);
    boolean isUserEligibleForDiscount(UUID userId);
    User authenticateUser(String email, String password);
}