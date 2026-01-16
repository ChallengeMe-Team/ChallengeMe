package challengeme.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Public DTO optimized for the competitive Ranking UI.
 * Includes user profile visuals and their calculated global rank.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardResponseDTO {
    private int rank;
    private String username;
    private String avatar;
    private Long totalPoints;
}