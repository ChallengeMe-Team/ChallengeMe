package challengeme.backend.dto.request.create;

import lombok.Data;
import java.util.UUID;

/**
 * Request DTO for creating a manual entry in the competitive leaderboard.
 * Facilitates the mapping between a user's identity and their initial scoring.
 */
@Data
public class LeaderboardCreateRequest {
    /** The UUID of the user being added to the ranking system. */
    private UUID userId;
    /** The starting point value for this specific leaderboard entry. */
    private int totalPoints;
}
