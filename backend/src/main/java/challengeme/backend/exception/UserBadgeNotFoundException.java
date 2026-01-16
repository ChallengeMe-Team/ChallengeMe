package challengeme.backend.exception;

/**
 * Exception thrown when an achievement record (UserBadge) is missing.
 * Occurs when trying to access or update a badge already earned by a user.
 */
public class UserBadgeNotFoundException extends NotFoundException {
    /**
     * @param message Detailed explanation regarding the missing achievement record.
     */
    public UserBadgeNotFoundException(String message) {
        super(message);
    }
}