package challengeme.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import challengeme.backend.model.User;
import challengeme.backend.model.Badge;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Junction entity that records the awarding of a Badge to a User.
 * Acts as a persistent record of a user's achievements and historical milestones.
 */
@Entity
@Table(name = "user_badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBadge {

    @Id
    @GeneratedValue
    private UUID id;

    /** The user who earned the achievement. Lazy loaded for performance. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User must be provided")
    private User user;

    /** The specific badge definition that was unlocked. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    @NotNull(message = "Badge must be provided")
    private Badge badge;

    /** The precise timestamp of when the criteria were met and the badge was issued. */
    @Column(name = "date_awarded", nullable = false)
    private LocalDateTime dateAwarded;
}
