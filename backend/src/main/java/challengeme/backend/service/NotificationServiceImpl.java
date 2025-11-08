package challengeme.backend.service;

import challengeme.backend.dto.CreateNotificationDto;
import challengeme.backend.dto.UpdateNotificationDto;
import challengeme.backend.model.Notification;
import challengeme.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementarea logicii de business pentru notificari.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(@Qualifier("inMemoryNotificationRepository") NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Optional<Notification> getNotificationById(UUID id) {
        return notificationRepository.findById(id);
    }

    @Override
    public List<Notification> getNotificationsByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public Notification createNotification(CreateNotificationDto dto) {
        // Mapam DTO-ul pe modelul nostru
        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
        notification.setMessage(dto.getMessage());
        notification.setType(dto.getType());

        // Repository-ul va seta ID-ul, timestamp-ul si isRead
        return notificationRepository.save(notification);
    }

    @Override
    public Optional<Notification> updateNotificationStatus(UUID id, UpdateNotificationDto dto) {
        Optional<Notification> existingNotification = notificationRepository.findById(id);

        if (existingNotification.isPresent()) {
            Notification notificationToUpdate = existingNotification.get();
            notificationToUpdate.setRead(dto.getIsRead());
            // Salvam notificarea actualizata
            // Repository-ul va actualiza timestamp-ul
            return Optional.of(notificationRepository.save(notificationToUpdate));
        }

        // Returnam empty daca notificarea nu a fost gasita
        return Optional.empty();
    }

    @Override
    public boolean deleteNotification(UUID id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return true; // Stergerea a avut succes
        }
        return false; // Notificarea nu a fost gasita
    }
}