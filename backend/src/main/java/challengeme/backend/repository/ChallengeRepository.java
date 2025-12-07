package challengeme.backend.repository;

import challengeme.backend.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    List<Challenge> findAllByCreatedBy(String createdBy);

    @Modifying // Spune Spring-ului că aceasta este o operațiune de UPDATE, nu SELECT
    @Transactional
    @Query("UPDATE Challenge c SET c.createdBy = :newUsername WHERE c.createdBy = :oldUsername")
    void updateCreatorUsername(String oldUsername, String newUsername);
}
    