package com.automovil_unite.backend.domain.repository;

import com.automovil_unite.backend.domain.model.PenaltyType;
import com.automovil_unite.backend.domain.model.UserPenalty;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPenaltyRepository {
    UserPenalty save(UserPenalty userPenalty);

    Optional<UserPenalty> findById(UUID id);

    List<UserPenalty> findByUserId(UUID userId);
}