package challengeme.backend.repository;

import challengeme.backend.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Badge entities.
 * Includes a complex native query to traverse the relationship between badges,
 * user-badge assignments, and users.
 */
public interface BadgeRepository extends JpaRepository<Badge, UUID> {

    /**
     * Retrieves all badges earned by a specific user using a three-table join.
     * @param username The identifier for the user.
     * @return List of Badge entities associated with the user.
     */
    @Query(value = "SELECT b.* FROM badges b " +
            "JOIN user_badges ub ON b.id = ub.badge_id " +
            "JOIN users u ON ub.user_id = u.id " +
            "WHERE u.username = :username", nativeQuery = true)
    List<Badge> findBadgesByUsername(@Param("username") String username);
}