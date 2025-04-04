package com.automovil_unite.backend.domain.service;

import com.automovil_unite.backend.domain.exception.EntityNotFoundException;
import com.automovil_unite.backend.domain.exception.UnauthorizedActionException;
import com.automovil_unite.backend.domain.model.Notification;
import com.automovil_unite.backend.domain.model.NotificationType;
import com.automovil_unite.backend.domain.model.Rental;
import com.automovil_unite.backend.domain.repository.NotificationRepository;
import com.automovil_unite.backend.domain.repository.RentalRepository;
import com.automovil_unite.backend.domain.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final RentalRepository rentalRepository;
    
    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            RentalRepository rentalRepository) {
        this.notificationRepository = notificationRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Notification> findById(UUID id) {
        return notificationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findByUserIdAndRead(UUID userId, boolean read) {
        return notificationRepository.findByUserIdAndRead(userId, read);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findByUserIdAndType(UUID userId, NotificationType type) {
        return notificationRepository.findByUserIdAndType(userId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public int countUnreadByUserId(UUID userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void markAsRead(UUID id) {
        notificationRepository.markAsRead(id);
    }

    @Override
    @Transactional
    public void deleteNotification(UUID id) {
        notificationRepository.delete(id);
    }

    @Override
    @Transactional
    public void sendRentalNotification(UUID rentalId, NotificationType type) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId.toString()));
        
        String title;
        String message;
        
        // Construir el título y mensaje según el tipo de notificación
        switch (type) {
            case RENTAL_REQUEST:
                title = "Nueva solicitud de alquiler";
                message = "Has recibido una nueva solicitud de alquiler para tu vehículo.";
                sendUserNotification(rental.getOwnerId(), title, message, type);
                break;
                
            case RENTAL_COUNTER_OFFER:
                title = "Contraoferta recibida";
                message = "Has recibido una contraoferta para tu solicitud de alquiler.";
                sendUserNotification(rental.getOwnerId(), title, message, type);
                break;
                
            case RENTAL_ACCEPTED:
                title = "Solicitud de alquiler aceptada";
                message = "Tu solicitud de alquiler ha sido aceptada.";
                sendUserNotification(rental.getTenantId(), title, message, type);
                break;
                
            case RENTAL_REJECTED:
                title = "Solicitud de alquiler rechazada";
                message = "Tu solicitud de alquiler ha sido rechazada.";
                sendUserNotification(rental.getTenantId(), title, message, type);
                break;
                
            case RENTAL_CONFIRMED:
                title = "Alquiler confirmado";
                message = "El alquiler ha sido confirmado con el código de verificación.";
                sendUserNotification(rental.getOwnerId(), title, message, type);
                sendUserNotification(rental.getTenantId(), title, message, type);
                break;
                
            case RENTAL_COMPLETED:
                title = "Alquiler completado";
                message = "El alquiler ha sido completado con éxito.";
                sendUserNotification(rental.getOwnerId(), title, message, type);
                sendUserNotification(rental.getTenantId(), title, message, type);
                break;
                
            case RENTAL_CANCELLED:
                title = "Alquiler cancelado";
                message = "El alquiler ha sido cancelado.";
                sendUserNotification(rental.getOwnerId(), title, message, type);
                sendUserNotification(rental.getTenantId(), title, message, type);
                break;
                
            case RENTAL_LATE:
                title = "Alquiler con retraso";
                message = "El vehículo no ha sido devuelto en el tiempo acordado.";
                sendUserNotification(rental.getOwnerId(), title, message, type);
                sendUserNotification(rental.getTenantId(), title, message, type);
                break;
                
            default:
                throw new IllegalArgumentException("Tipo de notificación no soportado");
        }
    }

    @Override
    @Transactional
    public void sendUserNotification(UUID userId, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
    }
}