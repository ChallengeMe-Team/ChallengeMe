package challengeme.backend.exception;

import java.util.UUID;

/**
 * Thrown when a specific Challenge entity cannot be found in the database.
 */
public class ChallengeNotFoundException extends NotFoundException {
    /**
     * @param id The unique identifier of the missing challenge.
     */
    public ChallengeNotFoundException(UUID id) {
        super("Challenge with id " + id + " not found");
    }
}
