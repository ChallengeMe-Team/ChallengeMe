package challengeme.backend.repository;

import challengeme.backend.model.ChallengeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeUserRepository extends JpaRepository<ChallengeUser, UUID> {
    List<ChallengeUser> findByUserId(UUID userId);

    void deleteAllByChallengeId(UUID challengeId);
}


