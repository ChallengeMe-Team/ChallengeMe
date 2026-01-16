package challengeme.backend.dto;

import challengeme.backend.model.NotificationType;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for the real-time notification system.
 * @param id Unique notification ID.
 * @param userId Recipient's unique identifier.
 * @param message The alert content.
 * @param type Category of the notification (e.g., CHALLENGE, SYSTEM).
 * @param timestamp Exact time when the notification was generated.
 * @param isRead Flag for tracking user interaction with the alert.
 */
public record NotificationDTO(
        UUID id,
        UUID userId,
        String message,
        NotificationType type,
        LocalDateTime timestamp,
        boolean isRead
) {}
