package challengeme.backend.repository;

import challengeme.backend.model.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/** Minimal repository for persistent leaderboard records. */
@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID> {
}
