package challengeme.backend.dto.request.update;

import challengeme.backend.model.ChallengeUserStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Request DTO for updating a user's specific progress on a challenge.
 * Manages the transition between statuses such as ACCEPTED, IN_PROGRESS, or COMPLETED.
 */
@Data
public class ChallengeUserUpdateRequest {
    /** The new status of the user's participation. */
    private ChallengeUserStatus status;
    /** The actual date when the user started working on the quest. */
    private LocalDateTime startDate;
    /** The manually adjusted deadline for finishing the challenge. */
    private LocalDate targetDeadline;
}