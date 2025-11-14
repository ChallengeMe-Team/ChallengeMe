package challengeme.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * FIȘIER NOU.
 * Excepție aruncată atunci când o notificare nu este găsită.
 * Este mapată automat pe un răspuns HTTP 404 Not Found
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(String message) {
        super(message);
    }

    public NotificationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
