package challengeme.backend.model;


import java.util.Objects;
import java.util.UUID;

public class Leaderboard {
    private UUID id;
    private User user;
    private int totalPoints;
    private int rank;

    public Leaderboard() {}
    public Leaderboard(UUID id, User user, int totalPoints) {
        this.id = id; this.user = user; this.totalPoints = totalPoints;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Leaderboard)) return false;
        return Objects.equals(id, ((Leaderboard) o).getId());
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
