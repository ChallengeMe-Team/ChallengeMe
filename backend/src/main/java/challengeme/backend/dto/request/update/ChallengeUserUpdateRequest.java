package challengeme.backend.dto.request.update;

import challengeme.backend.model.ChallengeUserStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ChallengeUserUpdateRequest {
    private ChallengeUserStatus status;
    private LocalDate startDate;

    private LocalDate targetDeadline;
}