package challengeme.backend.exception;

public class UserBadgeNotFoundException extends RuntimeException {

    public UserBadgeNotFoundException(String message) {
        super(message);
    }
}
