package com.automovil_unite.backend.domain.repository;

import com.automovil_unite.backend.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void delete(UUID id);
    boolean existsByEmail(String email);
    List<User> findByRating(double minRating);
}