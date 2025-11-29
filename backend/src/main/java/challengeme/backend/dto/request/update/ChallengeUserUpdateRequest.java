package challengeme.backend.dto.request.update;

import challengeme.backend.model.ChallengeUserStatus;
import lombok.Data;

@Data
public class ChallengeUserUpdateRequest {
    private ChallengeUserStatus status;
}
