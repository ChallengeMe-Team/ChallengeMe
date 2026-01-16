package challengeme.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a business rule conflict occurs (e.g., duplicate entries
 * or invalid state transitions). Automatically maps to HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    /**
     * @param message The specific conflict description.
     */
    public ConflictException(String message) {
        super(message);
    }
}