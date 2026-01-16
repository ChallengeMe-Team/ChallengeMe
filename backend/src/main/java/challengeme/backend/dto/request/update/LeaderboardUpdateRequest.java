package challengeme.backend.dto.request.update;

import lombok.Data;

/**
 * Request DTO used to manually adjust or synchronize a user's leaderboard score.
 */
@Data
public class LeaderboardUpdateRequest {
    private Integer totalPoints;
}
