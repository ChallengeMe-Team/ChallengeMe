package challengeme.backend.repository;

import challengeme.backend.model.Challenge;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChallengeRepository {
    List<Challenge> findAll();
    Optional<Challenge> findById(UUID id);
    Challenge save(Challenge challenge);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
    