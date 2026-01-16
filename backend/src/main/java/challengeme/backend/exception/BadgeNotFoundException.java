package challengeme.backend.exception;

/**
 * Exception thrown when a requested Badge cannot be found in the database.
 * Typically occurs during badge assignment or while viewing badge details.
 */
public class BadgeNotFoundException extends NotFoundException {
    /**
     * @param message Detailed description of the missing badge (e.g., name or custom error).
     */
    public BadgeNotFoundException(String message) {
        super(message);
    }
}