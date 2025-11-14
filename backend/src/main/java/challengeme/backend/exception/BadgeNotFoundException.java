package challengeme.backend.exception;

public class BadgeNotFoundException extends NotFoundException {
    public BadgeNotFoundException(String message) {
        super(message);
    }
}
