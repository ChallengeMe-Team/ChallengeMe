package challengeme.backend.repository;

import challengeme.backend.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    List<Challenge> findAllByCreatedBy(String createdBy);
}
    