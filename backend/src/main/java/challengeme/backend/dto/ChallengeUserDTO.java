package challengeme.backend.dto;

import challengeme.backend.model.ChallengeUserStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Detailed DTO representing the specific relationship between a User and a Challenge.
 * Combines challenge metadata with the user's progress and participation dates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeUserDTO {
    private UUID id;
    private UUID userId;
    private String username;

    /** Flattened challenge details to avoid extra API calls in the frontend. */
    private UUID challengeId;
    private String challengeTitle;
    private String description;
    private Integer points;
    private String category;
    private String difficulty;
    private String challengeCreatedBy;

    /** Tracking social assignment and completion stats. */
    private String assignedByUsername;
    private Integer timesCompleted;

    /** Current progress state and temporal tracking. */
    private ChallengeUserStatus status;
    private LocalDateTime startDate;
    private LocalDateTime dateAccepted;
    private LocalDateTime dateCompleted;
    private LocalDate deadline;
}