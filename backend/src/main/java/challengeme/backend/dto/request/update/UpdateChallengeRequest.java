package challengeme.backend.dto.request.update;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateChallengeRequest {
    private String status;         // Frontend trimite "IN_PROGRESS"
    private LocalDate startDate;    // Frontend trimite "startDate"
    private LocalDate targetDeadline; // Frontend trimite "targetDeadline"
}