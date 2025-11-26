package challengeme.backend.dto;

import challengeme.backend.model.ChallengeUserStatus;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeUserDTO {
    private UUID id;
    private UUID userId;
    private String username;
    private UUID challengeId;
    private String challengeTitle;
    private ChallengeUserStatus status;
    private LocalDate dateAccepted;
    private LocalDate dateCompleted;
}
