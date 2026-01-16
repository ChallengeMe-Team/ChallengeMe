package challengeme.backend.service;

import challengeme.backend.exception.NotificationNotFoundException;
import challengeme.backend.mapper.NotificationMapper;
import challengeme.backend.model.Notification;
import challengeme.backend.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import challengeme.backend.dto.request.update.NotificationUpdateRequest;
import challengeme.backend.dto.request.create.NotificationCreateRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing the notification lifecycle.
 * It handles the creation of alerts, retrieval of the user's inbox,
 * and status updates such as marking messages as read.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;

    /**
     * Retrieves all notifications in the system.
     * Primarily used for administrative monitoring.
     * @return a list of all Notification entities.
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    /**
     * Fetches the notification history for a specific user.
     * Used by the frontend to display the user's personalized alerts.
     * @param userId the UUID of the recipient.
     * @return a list of notifications belonging to the user.
     */
    public List<Notification> getNotificationsByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

    /**
     * Finds a single notification by its unique ID.
     * @param id the UUID of the notification.
     * @return the Notification entity.
     * @throws NotificationNotFoundException if the record is missing.
     */
    public Notification getNotificationById(UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
    }

    /**
     * Triggers the creation of a new alert.
     * Usually called by other services (e.g., ChallengeUserService) when a social event occurs.
     * @param request the DTO containing the message, recipient, and alert type.
     * @return the persisted Notification entity.
     */
    public Notification createNotification(NotificationCreateRequest request) {
        Notification entity = mapper.toEntity(request);
        return notificationRepository.save(entity);
    }

    /**
     * Updates an existing notification (e.g., toggling its 'read' status).
     * @param id the UUID of the notification.
     * @param request the DTO containing the update fields.
     * @return the updated Notification entity.
     */
    public Notification updateNotification(UUID id, NotificationUpdateRequest request) {
        Notification entity = getNotificationById(id); // aruncă excepție dacă nu există
        mapper.updateEntity(request, entity);
        return notificationRepository.save(entity);
    }

    /**
     * Permanently removes a notification from the database.
     * @param id the UUID of the notification to delete.
     */
    public void deleteNotification(UUID id) {
        Notification entity = getNotificationById(id); // aruncă excepție dacă nu există
        notificationRepository.delete(entity);
    }

    /**
     * Performs a bulk update operation to mark all unread notifications
     * for a user as read. Uses an optimized native query for performance.
     * @param userId the UUID of the user clearing their inbox.
     */
    @Transactional
    public void markAllNotificationsAsRead(UUID userId) {
        // Log used for monitoring high-volume social interactions
        System.out.println("DEBUG: Starting bulk update for user: " + userId);
        notificationRepository.markAllAsReadNative(userId);
        System.out.println("DEBUG: Native SQL sent to PostgreSQL successfully.");
    }
}