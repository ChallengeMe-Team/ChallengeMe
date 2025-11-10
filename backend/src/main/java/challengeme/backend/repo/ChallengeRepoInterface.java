package challengeme.backend.repo;

import challengeme.backend.domain.Challenge;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChallengeRepoInterface {
    List<Challenge> findAll();
    Optional<Challenge> findById(UUID id);
    Challenge save(Challenge challenge);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
