package com.automovil_unite.backend.application.service;

import com.automovil_unite.backend.application.dto.UserDto;
import com.automovil_unite.backend.application.dto.UserRegistrationDto;
import com.automovil_unite.backend.application.dto.UserUpdateDto;
import com.automovil_unite.backend.application.mapper.UserMapper;
import com.automovil_unite.backend.domain.event.DomainEventPublisher;
import com.automovil_unite.backend.domain.event.UserRegisteredEvent;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.exception.UserAlreadyExistsException;
import com.automovil_unite.backend.domain.model.User;
import com.automovil_unite.backend.domain.model.VerificationCode;
import com.automovil_unite.backend.domain.model.VerificationCodeType;
import com.automovil_unite.backend.domain.service.SecurityService;
import com.automovil_unite.backend.domain.service.UserService;
import com.automovil_unite.backend.domain.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserApplicationService {
    private final UserService userService;
    private final SecurityService securityService;
    private final VerificationService verificationService;
    private final DomainEventPublisher eventPublisher;
    private final UserMapper userMapper;

    @Transactional
    public UserDto registerUser(UserRegistrationDto registrationDto) {
        if (userService.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException(registrationDto.getEmail());
        }

        User user = userMapper.toEntity(registrationDto);
        user.setPassword(securityService.encodePassword(registrationDto.getPassword()));
        
        User savedUser = userService.registerUser(user);
        
        // Generate verification code and send email
        VerificationCode verificationCode = verificationService.generateCode(
                savedUser.getId(), VerificationCodeType.REGISTRATION);
        
        verificationService.sendVerificationEmail(
                savedUser.getId(), verificationCode.getCode(), VerificationCodeType.REGISTRATION);
        
        // Publish domain event
        eventPublisher.publish(new UserRegisteredEvent(savedUser));
        
        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id.toString()));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userService.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(UUID id, UserUpdateDto updateDto) {
        User user = userService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id.toString()));
        
        userMapper.updateEntity(user, updateDto);
        
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            user.setPassword(securityService.encodePassword(updateDto.getPassword()));
        }
        
        User updatedUser = userService.updateUser(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userService.findById(id).isPresent()) {
            throw new EntityNotFoundException("User", id.toString());
        }
        userService.deleteUser(id);
    }

    @Transactional(readOnly = true)
    public boolean isUserEligibleForDiscount(UUID userId) {
        return userService.isUserEligibleForDiscount(userId);
    }

    @Transactional
    public boolean verifyUserDocument(UUID userId, UUID documentId, boolean approved) {
        return userService.verifyUserDocument(userId, documentId, approved);
    }
}
