package challengeme.backend.dto.request.create;

import lombok.Data;
import java.util.UUID;

@Data
public class ChallengeUserCreateRequest {
    private UUID userId;
    private UUID challengeId;
}
