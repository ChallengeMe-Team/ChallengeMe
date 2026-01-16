package challengeme.backend.mapper;

import challengeme.backend.dto.request.create.NotificationCreateRequest;
import challengeme.backend.dto.NotificationDTO;
import challengeme.backend.dto.request.update.NotificationUpdateRequest;
import challengeme.backend.model.Notification;
import org.springframework.stereotype.Component;

/**
 * Mapper component responsible for converting Notification entities and DTOs.
 * It ensures that notification data, including timestamps and read status,
 * is correctly formatted for the frontend.
 */
@Component
public class NotificationMapper {

    /**
     * Converts a Notification entity into a NotificationDTO.
     * Maps all persistence fields to a data transfer object suitable for the UI inbox.
     * * @param entity The notification record from the database.
     * @return A DTO representing the notification alert.
     */
    public NotificationDTO toDTO(Notification entity) {
        return new NotificationDTO(
                entity.getId(),
                entity.getUserId(),
                entity.getMessage(),
                entity.getType(),
                entity.getTimestamp(),
                entity.isRead()
        );
    }

    /**
     * Maps a NotificationCreateRequest to a new Notification entity.
     * Automatically initializes the timestamp using the 'Europe/Bucharest' timezone
     * to ensure "Just now" time-ago calculations are accurate in the frontend.
     * * @param request The DTO containing the notification message and type.
     * @return A new Notification entity initialized as unread with the current timestamp.
     */
    public Notification toEntity(NotificationCreateRequest request) {
        Notification entity = new Notification();
        entity.setUserId(request.userId());
        entity.setMessage(request.message());
        entity.setType(request.type());
        entity.setTimestamp(java.time.ZonedDateTime.now(java.time.ZoneId.of("Europe/Bucharest")).toLocalDateTime());
        entity.setRead(false);
        return entity;
    }

    /**
     * Updates the status of an existing Notification entity.
     * Primarily used for marking notifications as read or unread.
     * * @param request The DTO containing the updated read status.
     * @param entity The target notification record to be modified.
     */
    public void updateEntity(NotificationUpdateRequest request, Notification entity) {
        if (request.isRead() != null) {
            entity.setRead(request.isRead());
        }
    }
}
