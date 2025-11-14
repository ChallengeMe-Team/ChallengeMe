package challengeme.backend.repository;

import challengeme.backend.model.ChallengeUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChallengeUserRepository {

    ChallengeUser save(ChallengeUser challengeUser);

    Optional<ChallengeUser> findById(UUID id);

    List<ChallengeUser> findAll();

    List<ChallengeUser> findByUserId(UUID userId);

    void deleteById(UUID id);
}

