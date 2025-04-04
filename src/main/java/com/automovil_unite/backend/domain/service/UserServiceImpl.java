package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.model.User;
import com.automovil_unite.backend.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    @Transactional
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        userRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public boolean verifyUserDocument(UUID userId, UUID documentId, boolean approved) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        // Aquí implementarías la lógica para verificar el documento específico
        // Esto es simplificado para el ejemplo, deberías buscar el documento y marcarlo como verificado
        
        return true;
    }

    @Override
    @Transactional
    public boolean applyPenalty(UUID userId, UUID rentalId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        user.setReportCount(user.getReportCount() + 1);
        userRepository.save(user);
        
        // Aquí implementarías la lógica para aplicar penalizaciones específicas
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserEligibleForRental(UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        // Verificar si el usuario tiene penalizaciones activas
        return !user.hasFuturePenalty();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserEligibleForDiscount(UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        return user.isEligibleForDiscount();
    }

    @Override
    public User authenticateUser(String email, String password) {
        // Esta implementación debe ser completada con la lógica real de autenticación
        // Aquí solo se muestra un esquema básico
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return null;
        }
        
        User user = userOpt.get();
        // En una implementación real, verificarías el password con un PasswordEncoder
        // Aquí simplificamos para el ejemplo
        return user;
    }
}