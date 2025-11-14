package challengeme.backend.exception;

import java.util.UUID;

public class LeaderboardNotFoundException extends NotFoundException {

    public LeaderboardNotFoundException(UUID id) {
        super("Leaderboard entry not found with id: " + id);
    }

    public LeaderboardNotFoundException(String message) {
        super(message);
    }
}

