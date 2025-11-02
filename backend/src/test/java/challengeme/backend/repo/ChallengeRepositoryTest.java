package challengeme.backend.repo;

import challengeme.backend.domain.Challenge;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class ChallengeRepositoryTest {

    private ChallengeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ChallengeRepository();
    }

    @Test
    public void testSaveAndFindById() {
        Challenge challenge = new Challenge(
                "Sorting",
                "Implement bubble sort",
                "Algorithms",
                Challenge.Difficulty.EASY,
                50,
                "Admin"
        );

        repository.save(challenge);

        Optional<Challenge> found = Optional.ofNullable(repository.findById(challenge.getId()));
        assertTrue(found.isPresent(), "Challenge should be found after saving");
        assertEquals("Sorting", found.get().getTitle());
        assertEquals(Challenge.Difficulty.EASY, found.get().getDifficulty());
    }

    @Test
    public void testFindAllReturnsAllChallenges() {
        Challenge c1 = new Challenge("Arrays", "Array reversal", "Data Structures", Challenge.Difficulty.MEDIUM, 100, "User1");
        Challenge c2 = new Challenge("Graphs", "DFS traversal", "Algorithms", Challenge.Difficulty.HARD, 200, "User2");

        repository.save(c1);
        repository.save(c2);

        List<Challenge> all = repository.findAll();
        assertEquals(2, all.size(), "Repository should contain 2 challenges");
    }

    @Test
    public void testDeleteByIdRemovesChallenge() {
        Challenge challenge = new Challenge("Binary Search", "Implement binary search", "Algorithms", Challenge.Difficulty.MEDIUM, 150, "Admin");
        repository.save(challenge);

        repository.deleteById(challenge.getId());
        Optional<Challenge> found = Optional.ofNullable(repository.findById(challenge.getId()));

        assertFalse(found.isPresent(), "Challenge should be deleted");
    }

    @Test
    public void testExistsById() {
        Challenge challenge = new Challenge("Recursion", "Solve factorial recursively", "Math", Challenge.Difficulty.MEDIUM, 75, "Admin");
        repository.save(challenge);

        assertTrue(repository.existsById(challenge.getId()));
        repository.deleteById(challenge.getId());
        assertFalse(repository.existsById(challenge.getId()));
    }
}
