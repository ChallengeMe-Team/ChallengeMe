package challengeme.backend.exceptions;

import java.util.UUID;

public class ChallengeNotFoundException extends RuntimeException {
    public ChallengeNotFoundException(UUID id) {
        super("Challenge with id " + id + " not found");
    }
}
