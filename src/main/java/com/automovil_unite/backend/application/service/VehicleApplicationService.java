package com.automovil_unite.backend.application.service;

import com.automovil_unite.backend.application.dto.VehicleDto;
import com.automovil_unite.backend.application.dto.VehiclePhotoDto;
import com.automovil_unite.backend.application.dto.VehicleRegistrationDto;
import com.automovil_unite.backend.application.dto.VehicleUpdateDto;
import com.automovil_unite.backend.application.mapper.VehicleMapper;
import com.automovil_unite.backend.application.mapper.VehiclePhotoMapper;
import com.automovil_unite.backend.domain.event.DomainEventPublisher;
import com.automovil_unite.backend.domain.event.VehicleRegisteredEvent;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.exception.UnauthorizedActionException;
import com.automovil_unite.backend.domain.model.Vehicle;
import com.automovil_unite.backend.domain.model.VehiclePhoto;
import com.automovil_unite.backend.domain.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleApplicationService {
    private final VehicleService vehicleService;
    private final DomainEventPublisher eventPublisher;
    private final VehicleMapper vehicleMapper;
    private final VehiclePhotoMapper photoMapper = VehiclePhotoMapper.INSTANCE;

    @Transactional
    public VehicleDto registerVehicle(UUID ownerId, VehicleRegistrationDto registrationDto) {
        Vehicle vehicle = vehicleMapper.toEntity(registrationDto);
        vehicle.setOwnerId(ownerId);

        Vehicle savedVehicle = vehicleService.registerVehicle(vehicle);

        // Publish domain event
        eventPublisher.publish(new VehicleRegisteredEvent(savedVehicle));

        return vehicleMapper.toDto(savedVehicle);
    }

    @Transactional(readOnly = true)
    public VehicleDto getVehicleById(UUID id) {
        Vehicle vehicle = vehicleService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id.toString()));
        return vehicleMapper.toDto(vehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleDto> getVehiclesByOwnerId(UUID ownerId) {
        return vehicleService.findByOwnerId(ownerId).stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleDto> getAllVehicles() {
        return vehicleService.findAll().stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleDto> getTopRentedVehicles(int limit) {
        return vehicleService.findTopRented(limit).stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleDto> getTopByBrand(int limit) {
        return vehicleService.findTopByBrand(limit).stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleDto> getAvailableVehicles(LocalDateTime startDate, LocalDateTime endDate) {
        return vehicleService.findAvailable(startDate, endDate).stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public VehicleDto updateVehicle(UUID id, UUID currentUserId, VehicleUpdateDto updateDto) {
        Vehicle vehicle = vehicleService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id.toString()));

        if (!vehicle.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException(currentUserId, "vehicle", "update");
        }

        vehicleMapper.updateEntity(vehicle, updateDto);
        Vehicle updatedVehicle = vehicleService.updateVehicle(vehicle);
        return vehicleMapper.toDto(updatedVehicle);
    }
    @Transactional
    public void deleteVehicle(UUID id, UUID currentUserId) {
        Vehicle vehicle = vehicleService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id.toString()));

        if (!vehicle.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException(currentUserId, "vehicle", "delete");
        }

        vehicleService.deleteVehicle(id);
    }

    @Transactional
    public VehicleDto addPhoto(UUID vehicleId, UUID currentUserId, VehiclePhotoDto photoDto) {
        Vehicle vehicle = vehicleService.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId.toString()));

        if (!vehicle.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException(currentUserId, "vehicle", "add photo");
        }

        VehiclePhoto photo = photoMapper.toEntity(photoDto);
        photo.setVehicleId(vehicleId);

        Vehicle updatedVehicle = vehicleService.addPhoto(vehicleId, photo);
        return vehicleMapper.toDto(updatedVehicle);
    }

    @Transactional
    public VehicleDto removePhoto(UUID vehicleId, UUID photoId, UUID currentUserId) {
        Vehicle vehicle = vehicleService.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId.toString()));

        if (!vehicle.getOwnerId().equals(currentUserId)) {
            throw new UnauthorizedActionException(currentUserId, "vehicle", "remove photo");
        }

        Vehicle updatedVehicle = vehicleService.removePhoto(vehicleId, photoId);
        return vehicleMapper.toDto(updatedVehicle);
    }

    @Transactional
    public VehicleDto verifyVehicle(UUID vehicleId, boolean approved) {
        Vehicle vehicle = vehicleService.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId.toString()));

        vehicleService.verifyVehicle(vehicleId, approved);

        return vehicleMapper.toDto(vehicle);
    }
}