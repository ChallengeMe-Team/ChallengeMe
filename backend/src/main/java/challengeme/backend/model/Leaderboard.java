package challengeme.backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "leaderboard")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leaderboard {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int totalPoints;

    @Column(nullable = false)
    private int rank;

    public Leaderboard(User user, int totalPoints) {
        this.user = user;
        this.totalPoints = totalPoints;
    }
}
