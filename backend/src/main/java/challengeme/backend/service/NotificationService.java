package challengeme.backend.service;

import challengeme.backend.exception.NotificationNotFoundException;
import challengeme.backend.model.Notification;
import challengeme.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * FIȘIER MODIFICAT MASIV
 * - Acesta este noul fișier de service (fostul NotificationServiceImpl).
 * - Interfața a fost eliminată.
 * - Folosește @RequiredArgsConstructor pentru injecție.
 * - Nu mai folosește DTO-uri.
 * - Aruncă NotificationNotFoundException.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    // Injecție prin constructor (Lombok)
    private final NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

    public Notification getNotificationById(UUID id) {
        // Logica s-a schimbat: aruncăm excepție dacă nu găsim
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
    }

    public Notification createNotification(Notification notification) {
        // Logica de creare (fără DTO)
        // Setăm valorile default
        if (notification.getId() == null) {
            notification.setId(UUID.randomUUID());
        }
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false); // Setează isRead la false implicit

        return notificationRepository.save(notification);
    }

    public Notification updateNotificationStatus(UUID id, Map<String, Object> updates) {
        // Găsim notificarea (aruncă excepție dacă nu există)
        Notification notification = getNotificationById(id);

        // Aplicăm update-ul parțial (doar pentru 'isRead')
        if (updates.containsKey("isRead")) {
            Object isReadValue = updates.get("isRead");
            if (isReadValue instanceof Boolean) {
                notification.setRead((Boolean) isReadValue);
            }
        }
        // Aici se pot adăuga și alte câmpuri dacă e nevoie

        return notificationRepository.save(notification);
    }

    public void deleteNotification(UUID id) {
        // Verificăm dacă există înainte de a șterge
        if (!notificationRepository.existsById(id)) {
            throw new NotificationNotFoundException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }
}