package challengeme.backend.dto.request.update;

import lombok.Data;
import java.time.LocalDate;

/**
 * Specialized DTO used by the frontend to initiate or adjust challenge participation.
 * Bridges the gap between the UI input and the backend status management.
 */
@Data
public class UpdateChallengeRequest {
    /** Status identifier sent from the UI (e.g., "IN_PROGRESS"). */
    private String status;
    /** The date provided by the user to start the quest. */
    private LocalDate startDate;
    /** The target date by which the user intends to finish. */
    private LocalDate targetDeadline;
}