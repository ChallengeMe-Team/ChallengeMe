package challengeme.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "challenge_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeUser {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    @NotNull
    private Challenge challenge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeUserStatus status = ChallengeUserStatus.RECEIVED;

    private LocalDateTime dateAccepted;
    private LocalDateTime dateCompleted;

    private LocalDate startDate;

    private LocalDate deadline;

    @Column(nullable = true)
    private UUID assignedBy;

    private Integer timesCompleted = 0;

}