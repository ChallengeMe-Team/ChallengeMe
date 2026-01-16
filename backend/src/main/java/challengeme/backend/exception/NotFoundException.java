package challengeme.backend.exception;

/**
 * Base abstract exception for all 'Resource Not Found' scenarios in the system.
 * Extends RuntimeException to allow for unchecked exception handling within the business layer.
 */
public class NotFoundException extends RuntimeException {
    /**
     * @param message Detailed explanation of which resource was missing.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
