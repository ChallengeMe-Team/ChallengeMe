package challengeme.backend.exception;

import java.util.UUID;

/**
 * Exception thrown when a Leaderboard entry is missing for a specific user or ID.
 */
public class LeaderboardNotFoundException extends NotFoundException {
    /**
     * @param id The unique UUID of the leaderboard entry that could not be located.
     */
    public LeaderboardNotFoundException(UUID id) {
        super("Leaderboard entry not found with id: " + id);
    }
}