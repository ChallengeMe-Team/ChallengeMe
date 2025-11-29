package challengeme.backend.dto;

import lombok.*;

import java.util.UUID;

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
