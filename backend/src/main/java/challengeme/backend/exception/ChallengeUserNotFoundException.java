package challengeme.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepție standard pentru a indica HTTP 404 Not Found.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ChallengeUserNotFoundException extends NotFoundException {

    public ChallengeUserNotFoundException(String message) {
        super(message);
    }
}
