package challengeme.backend.dto;

import lombok.*;

import java.util.UUID;

/**
 * Internal DTO for leaderboard data management and ranking calculation.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardDTO {
    private UUID id;
    private UUID userId;
    private String username;
    private int totalPoints;
    private int rank;
}
