package challengeme.backend.repository;

import challengeme.backend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

/**
 * Repository for User account data. Supports authentication and profile statistics updates.
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    /** Supports multi-credential login (email or username). */    Optional<User> findByUsernameOrEmail(String username, String email);
    @Query("SELECT u.username, u.avatar, u.points FROM User u ORDER BY u.points DESC")
    List<Object[]> findGlobalLeaderboard();

    /**
     * Atomic operation to increment user level/XP upon challenge completion.
     * Uses COALESCE to prevent null pointer issues during calculation.
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET " +
            "u.totalCompletedChallenges = COALESCE(u.totalCompletedChallenges, 0) + 1, " +
            "u.points = COALESCE(u.points, 0) + :xpPoints " +
            "WHERE u.id = :userId")
    void incrementMissionsAndXP(@Param("userId") UUID userId, @Param("xpPoints") Integer xpPoints);
}
