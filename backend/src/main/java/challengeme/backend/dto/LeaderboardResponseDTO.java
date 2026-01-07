package challengeme.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardResponseDTO {
    private int rank;
    private String username;
    private String avatar;
    private Long totalPoints;
}