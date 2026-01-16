package challengeme.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the link between a User and a Challenge is not found.
 * Explicitly mapped to HTTP 404 to handle quest progress tracking errors.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ChallengeUserNotFoundException extends NotFoundException {
    /**
     * @param message Details about the missing participation record.
     */
    public ChallengeUserNotFoundException(String message) {
        super(message);
    }
}