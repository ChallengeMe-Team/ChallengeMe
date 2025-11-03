package challengeme.backend.repo;

import challengeme.backend.domain.Challenge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeRepositoryTest {

    private ChallengeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ChallengeRepository();
    }

    @Test
    void shouldSaveAndFindChallenge() {
        Challenge challenge = new Challenge(
                "Test Challenge", "Description", "Fitness",
                Challenge.Difficulty.MEDIUM, 150, "user123"
        );

        Challenge saved = repository.save(challenge);
        Optional<Challenge> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getTitle(), found.get().getTitle());
    }

    @Test
    void shouldReturnEmptyWhenChallengeNotFound() {
        Optional<Challenge> found = repository.findById(UUID.randomUUID());
        assertFalse(found.isPresent());
    }
}