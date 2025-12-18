package challengeme.backend.repository;

import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.ChallengeUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeUserRepository extends JpaRepository<ChallengeUser, UUID> {
    List<ChallengeUser> findByUserId(UUID userId);

    boolean existsByUserIdAndChallengeId(UUID userId, UUID challengeId);

    void deleteAllByChallengeId(UUID challengeId);

    List<ChallengeUser> findByUserIdAndStatus(UUID userId, ChallengeUserStatus status);
}