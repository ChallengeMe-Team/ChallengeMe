package challengeme.backend.exception;

import java.util.UUID;

public class ChallengeNotFoundException extends NotFoundException {
    public ChallengeNotFoundException(UUID id) {
        super("Challenge with id " + id + " not found");
    }
}
