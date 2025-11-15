package challengeme.backend.repository;

import challengeme.backend.model.Challenge;
import challengeme.backend.model.Challenge.Difficulty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ChallengeJPARepositoryTests {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Test
    void testSaveAndFindById() {
        Challenge challenge = new Challenge(null, "Title", "Desc", "Cat", Difficulty.MEDIUM, 100, "User");
        Challenge saved = challengeRepository.save(challenge);
        Challenge found = challengeRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Title", found.getTitle());
    }

    @Test
    void testUpdate() {
        Challenge c = challengeRepository.save(new Challenge(null, "Old", "Desc", "Cat", Difficulty.EASY, 50, "User"));
        c.setTitle("Updated");
        challengeRepository.save(c);
        Challenge updated = challengeRepository.findById(c.getId()).orElseThrow();
        assertEquals("Updated", updated.getTitle());
    }

    @Test
    void testDelete() {
        Challenge c = challengeRepository.save(new Challenge(null, "T", "D", "C", Difficulty.HARD, 200, "U"));
        UUID id = c.getId();
        challengeRepository.deleteById(id);
        assertTrue(challengeRepository.findById(id).isEmpty());
    }

    @Test
    void testFindAll() {
        challengeRepository.save(new Challenge(null, "C1", "D1", "Cat1", Difficulty.EASY, 10, "U1"));
        challengeRepository.save(new Challenge(null, "C2", "D2", "Cat2", Difficulty.MEDIUM, 20, "U2"));

        assertEquals(2, challengeRepository.findAll().size());
    }

    @Test
    void testDeleteNonExisting() {
        UUID randomId = UUID.randomUUID();
        assertDoesNotThrow(() -> challengeRepository.deleteById(randomId));
        assertTrue(challengeRepository.findAll().isEmpty());
    }

    @Test
    void testSaveNullTitle() {
        Challenge c = new Challenge(null, null, "Desc", "Cat", Difficulty.EASY, 10, "U");
        assertThrows(Exception.class, () -> challengeRepository.saveAndFlush(c));
    }
}
