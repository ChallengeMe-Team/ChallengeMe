package challengeme.backend.repository;

import challengeme.backend.model.Leaderboard;

import java.util.List;
import java.util.UUID;

public interface LeaderboardRepository {
    List<Leaderboard> findAll();
    Leaderboard findById(UUID id);
    Leaderboard save(Leaderboard entry);   // doar CREATE
    void delete(UUID id);
    void update(Leaderboard entry);        // pentru UPDATE explicit
}
