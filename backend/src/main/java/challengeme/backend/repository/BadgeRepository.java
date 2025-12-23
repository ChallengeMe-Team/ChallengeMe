package challengeme.backend.repository;

import challengeme.backend.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface BadgeRepository extends JpaRepository<Badge, UUID> {

    // Query nativ care leagă tabelele: badges -> user_badges -> users
    @Query(value = "SELECT b.* FROM badges b " +
            "JOIN user_badges ub ON b.id = ub.badge_id " +
            "JOIN users u ON ub.user_id = u.id " +
            "WHERE u.username = :username", nativeQuery = true)
    List<Badge> findBadgesByUsername(@Param("username") String username);
}