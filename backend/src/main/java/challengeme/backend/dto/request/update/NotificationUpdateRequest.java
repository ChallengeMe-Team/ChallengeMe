package challengeme.backend.dto.request.update;

/**
 * Compact DTO for managing notification states.
 * Primarily used to mark alerts as read within the user's inbox.
 * @param isRead Boolean flag indicating the read status of the notification.
 */
public record NotificationUpdateRequest(
        Boolean isRead
) {}
