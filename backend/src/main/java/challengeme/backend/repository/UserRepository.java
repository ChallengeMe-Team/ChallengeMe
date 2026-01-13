package challengeme.backend.repository;

import challengeme.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

// Avem nevoie de căutare flexibilă pentru login

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // Pentru login cu email SAU username
    Optional<User> findByUsernameOrEmail(String username, String email);
    @Query("SELECT u.username, u.avatar, u.points FROM User u ORDER BY u.points DESC")
    List<Object[]> findGlobalLeaderboard();
}
