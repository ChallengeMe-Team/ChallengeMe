package challengeme.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Entity representing the 'leaderboard' table.
 * Used to store aggregated points and calculated ranks for a competitive view.
 */
@Entity
@Table(name = "leaderboard")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leaderboard {

    @Id
    @GeneratedValue
    private UUID id;

    /** The user whose rank is being tracked. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Total accumulated points for the ranking entry. */
    @Column(nullable = false)
    private int totalPoints;

    /** The calculated numerical position in the global or filtered ranking. */
    @Column(nullable = false)
    private int rank;

    /**
     * Simplified constructor for creating a ranking entry for a user.
     */
    public Leaderboard(User user, int totalPoints) {
        this.user = user;
        this.totalPoints = totalPoints;
    }
}
