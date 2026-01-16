package challengeme.backend.exception;

/**
 * Exception thrown when a specific Notification record does not exist.
 * Used when marking a notification as read or deleting it from the inbox.
 */
public class NotificationNotFoundException extends NotFoundException {
    /**
     * @param message Information about the specific notification that was not found.
     */
    public NotificationNotFoundException(String message) {
        super(message);
    }
}