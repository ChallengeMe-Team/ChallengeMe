package challengeme.backend.dto.request.create;

import lombok.Data;
import java.util.UUID;

@Data
public class LeaderboardCreateRequest {
    private UUID userId;
    private int totalPoints;
}
