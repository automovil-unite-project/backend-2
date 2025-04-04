package com.automovil_unite.backend.infrastructure.persistence.repository;

import com.automovil_unite.backend.domain.model.User;
import com.automovil_unite.backend.domain.repository.UserRepository;
import com.automovil_unite.backend.infrastructure.persistence.entity.UserEntity;
import com.automovil_unite.backend.infrastructure.persistence.mapper.PersistenceUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final PersistenceUserMapper userMapper = PersistenceUserMapper.INSTANCE;


    @Override
    public User save(User user) {
        UserEntity entity;
        if (user.getId() != null) {
            entity = jpaUserRepository.findById(user.getId())
                    .orElse(new UserEntity());
            userMapper.updateEntity(entity, user);
        } else {
            entity = userMapper.toEntity(user);
        }
        return userMapper.toDomain(jpaUserRepository.save(entity));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public List<User> findByRating(double minRating) {
        return jpaUserRepository.findByRatingGreaterThanEqual(minRating).stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }
}
