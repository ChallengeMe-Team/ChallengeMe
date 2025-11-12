package challengeme.backend.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Leaderboard {
    private UUID id;
    private User user;
    private int totalPoints;
    private int rank;


    public Leaderboard(UUID id, User user, int totalPoints) {
        this.id = id; this.user = user; this.totalPoints = totalPoints;
    }



    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Leaderboard)) return false;
        return Objects.equals(id, ((Leaderboard) o).getId());
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
