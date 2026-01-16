package challengeme.backend.repository;

import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.ChallengeUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Core repository for managing user-challenge interactions and rankings.
 */
@Repository
public interface ChallengeUserRepository extends JpaRepository<ChallengeUser, UUID> {
    List<ChallengeUser> findByUserId(UUID userId);

    /**
     * Aggregates points for a dynamic leaderboard based on a rolling start date.
     * Uses COALESCE and conditional aggregation to rank users even with zero points.
     * @param startDate Filter for completion date (used for Weekly/Monthly rankings).
     * @return A list of Object arrays [username, avatar, totalPoints].
     */
    @Query("SELECT u.username, u.avatar, " +
            "COALESCE(SUM(CASE WHEN cu.status = 'COMPLETED' AND cu.dateCompleted >= CAST(:startDate AS timestamp) THEN c.points ELSE 0 END), 0) " +
            "FROM User u " +
            "LEFT JOIN ChallengeUser cu ON u.id = cu.user.id " +
            "LEFT JOIN Challenge c ON c.id = cu.challenge.id " +
            "GROUP BY u.id, u.username, u.avatar " +
            "ORDER BY 3 DESC")
    List<Object[]> aggregateRankings(@Param("startDate") LocalDate startDate);

    boolean existsByUserIdAndChallengeId(UUID userId, UUID challengeId);


    /** Checks if a user is currently engaged in a challenge with specific active statuses. */
    @Query("SELECT CASE WHEN COUNT(cu) > 0 THEN true ELSE false END FROM ChallengeUser cu " +
            "WHERE cu.user.id = :userId " +
            "AND cu.challenge.id = :challengeId " +
            "AND cu.status IN :statuses")
    boolean isChallengeActiveForUser(@Param("userId") UUID userId,
                                     @Param("challengeId") UUID challengeId,
                                     @Param("statuses") Collection<ChallengeUserStatus> statuses);

    Optional<ChallengeUser> findByUserIdAndChallengeId(UUID userId, UUID challengeId);

    void deleteAllByChallengeId(UUID challengeId);

    List<ChallengeUser> findByUserIdAndStatus(UUID userId, ChallengeUserStatus status);
}