package challengeme.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing the 'challenge_users' table.
 * Tracks the specific relationship and progress of a user for a particular challenge.
 * This is a junction table between User and Challenge entities.
 */
@Entity
@Table(name = "challenge_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeUser {

    @Id
    @GeneratedValue
    private UUID id;

    /** The user associated with this participation record. Lazy loaded for performance. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    /** The specific challenge the user is participating in. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    @NotNull
    private Challenge challenge;

    /** Current state of progress, initialized as RECEIVED by default. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeUserStatus status = ChallengeUserStatus.RECEIVED;

    /** Timestamp when the user formally accepted the quest. */
    private LocalDateTime dateAccepted;

    /** Timestamp when the quest was successfully completed. */
    private LocalDateTime dateCompleted;

    /** The date the user intended to start the challenge. */
    private LocalDateTime startDate;

    /** The deadline set for the completion of the challenge. */
    private LocalDate deadline;

    /** The UUID of the user who assigned this challenge (if applicable). Null if self-started. */
    @Column(nullable = true)
    private UUID assignedBy;

    /** Counter for how many times this specific user has completed this specific quest. */
    private Integer timesCompleted = 0;

}