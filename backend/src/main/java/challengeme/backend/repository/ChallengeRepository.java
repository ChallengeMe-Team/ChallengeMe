package challengeme.backend.repository;

import challengeme.backend.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing Challenge definitions.
 */
public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    /** Finds all challenges created by a specific user. */
    List<Challenge> findAllByCreatedBy(String createdBy);

    /**
     * Batch updates the 'createdBy' field when a user changes their username.
     * Demonstrates data consistency maintenance across the platform.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Challenge c SET c.createdBy = :newUsername WHERE c.createdBy = :oldUsername")
    void updateCreatorUsername(String oldUsername, String newUsername);
}
    