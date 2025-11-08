package challengeme.backend.repository;


import challengeme.backend.model.Leaderboard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaderboardRepository {
    Leaderboard save(Leaderboard entry);           // create/update
    Optional<Leaderboard> findById(UUID id);
    List<Leaderboard> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}

