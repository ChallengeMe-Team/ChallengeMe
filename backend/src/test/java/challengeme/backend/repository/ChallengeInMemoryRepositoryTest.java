package challengeme.backend.repository;

import challengeme.backend.model.Challenge;
import challengeme.backend.repository.inMemory.InMemoryChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeInMemoryRepositoryTest {

    private InMemoryChallengeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryChallengeRepository();
    }

    @Test
    void shouldSaveAndFindChallenge() {
        Challenge challenge = new Challenge("T", "D", "C", Challenge.Difficulty.MEDIUM, 150, "U");
        Challenge saved = repository.save(challenge);

        assertTrue(repository.findById(saved.getId()).isPresent());
    }

    @Test
    void shouldUpdateExistingChallenge() {
        Challenge c1 = new Challenge("Old", "D", "C", Challenge.Difficulty.EASY, 10, "U");
        repository.save(c1);
        c1.setTitle("Updated");
        repository.save(c1);

        assertEquals("Updated", repository.findById(c1.getId()).get().getTitle());
    }

    @Test
    void shouldDeleteById() {
        Challenge c = new Challenge("T", "D", "C", Challenge.Difficulty.MEDIUM, 150, "U");
        repository.save(c);
        repository.deleteById(c.getId());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void shouldCheckExistsById() {
        Challenge c = new Challenge("T", "D", "C", Challenge.Difficulty.HARD, 100, "U");
        repository.save(c);
        assertTrue(repository.existsById(c.getId()));
    }
}
