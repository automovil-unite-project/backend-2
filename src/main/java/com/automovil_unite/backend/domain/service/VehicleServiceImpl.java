package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.event.DomainEventPublisher;
import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.model.Vehicle;
import com.automovil_unite.backend.domain.model.VehiclePhoto;
import com.automovil_unite.backend.domain.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VehicleServiceImpl implements VehicleService {
    
    private final VehicleRepository vehicleRepository;
    private final DomainEventPublisher eventPublisher;
    
    public VehicleServiceImpl(VehicleRepository vehicleRepository, DomainEventPublisher eventPublisher) {
        this.vehicleRepository = vehicleRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Vehicle registerVehicle(Vehicle vehicle) {
        // Validaciones adicionales podrían ir aquí
        vehicle.setRating(0.0);
        vehicle.setReviewCount(0);
        vehicle.setRentCount(0);
        vehicle.setAvailable(true);
        vehicle.setVerified(false); // Requiere verificación administrativa
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        
        // Aquí se publicaría un evento de dominio
        // eventPublisher.publish(new VehicleRegisteredEvent(savedVehicle));
        
        return savedVehicle;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> findById(UUID id) {
        return vehicleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findByOwnerId(UUID ownerId) {
        return vehicleRepository.findByOwnerId(ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findTopRented(int limit) {
        List<Vehicle> allVehicles = vehicleRepository.findTopRented(limit);
        return allVehicles.size() > limit ? allVehicles.subList(0, limit) : allVehicles;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findTopByBrand(int limit) {
        List<Vehicle> allVehicles = vehicleRepository.findTopByBrand(limit);
        return allVehicles.size() > limit ? allVehicles.subList(0, limit) : allVehicles;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailable(LocalDateTime startDate, LocalDateTime endDate) {
        return vehicleRepository.findAvailable(startDate, endDate);
    }

    @Override
    @Transactional
    public Vehicle updateVehicle(Vehicle vehicle) {
        // Verificar que el vehículo existe
        vehicleRepository.findById(vehicle.getId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicle.getId().toString()));
        
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(UUID id) {
        // Verificar que el vehículo existe
        vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id.toString()));
        
        vehicleRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleAvailable(UUID vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            return false;
        }
        
        Vehicle vehicle = vehicleOpt.get();
        return vehicle.isAvailableForRent(startDate, endDate);
    }

    @Override
    @Transactional
    public Vehicle addPhoto(UUID vehicleId, VehiclePhoto photo) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId.toString()));
        
        // Establecer el ID del vehículo en la foto
        photo.setVehicleId(vehicleId);
        
        // Si es la primera foto o está marcada como principal, asegurarse de que sea la única principal
        if (photo.isMain() || vehicle.getPhotos().isEmpty()) {
            photo.setMain(true);
            // Desmarcar otras fotos como principales
            vehicle.getPhotos().forEach(p -> p.setMain(false));
        }
        
        vehicle.getPhotos().add(photo);
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public Vehicle removePhoto(UUID vehicleId, UUID photoId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId.toString()));
        
        boolean removed = vehicle.getPhotos().removeIf(photo -> photo.getId().equals(photoId));
        
        if (!removed) {
            throw new EntityNotFoundException("Photo", photoId.toString());
        }
        
        // Si se eliminó la foto principal y hay otras fotos, establecer una nueva foto principal
        boolean hasMainPhoto = vehicle.getPhotos().stream().anyMatch(VehiclePhoto::isMain);
        if (!hasMainPhoto && !vehicle.getPhotos().isEmpty()) {
            vehicle.getPhotos().iterator().next().setMain(true);
        }
        
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public boolean verifyVehicle(UUID vehicleId, boolean approved) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId.toString()));
        
        vehicle.setVerified(approved);
        vehicleRepository.save(vehicle);
        
        // Publicar evento de verificación
        // eventPublisher.publish(new VehicleVerifiedEvent(vehicleId, approved));
        
        return approved;
    }
}