package challengeme.backend.exception;

/**
 * Exception thrown when a User entity is missing from the system.
 * Essential for security and profile management flows.
 */
public class UserNotFoundException extends NotFoundException {
    /**
     * @param message Description identifying the missing user (usually by username or ID).
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}