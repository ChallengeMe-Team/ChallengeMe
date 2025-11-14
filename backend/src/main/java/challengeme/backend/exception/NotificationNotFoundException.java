package challengeme.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * FIȘIER NOU.
 * Excepție aruncată atunci când o notificare nu este găsită.
 * Este mapată automat pe un răspuns HTTP 404 Not Found
 */

public class NotificationNotFoundException extends NotFoundException {
    public NotificationNotFoundException(String message) {
        super(message);
    }

}
