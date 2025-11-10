package challengeme.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeUser {
    private UUID id;
    private UUID userId;
    private UUID challengeId;
    private ChallengeUserStatus status;
    private LocalDate dateAccepted;
    private LocalDate dateCompleted;
}
