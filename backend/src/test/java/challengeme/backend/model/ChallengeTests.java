package challengeme.backend.model;

import challengeme.backend.repository.ChallengeRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ChallengeTests {

    private static Validator validator;

    @Autowired
    private ChallengeRepository challengeRepository;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ================================
    // MODEL / VALIDATION TESTS
    // ================================

    @Test
    void testChallengeGettersSetters() {
        Challenge challenge = new Challenge();
        challenge.setTitle("Test Challenge");
        challenge.setDescription("Desc");
        challenge.setCategory("Fitness");
        challenge.setDifficulty(Challenge.Difficulty.MEDIUM);
        challenge.setPoints(50);
        challenge.setCreatedBy("user123");

        assertEquals("Test Challenge", challenge.getTitle());
        assertEquals("Desc", challenge.getDescription());
        assertEquals("Fitness", challenge.getCategory());
        assertEquals(Challenge.Difficulty.MEDIUM, challenge.getDifficulty());
        assertEquals(50, challenge.getPoints());
        assertEquals("user123", challenge.getCreatedBy());
    }

    @Test
    void testChallengeAllArgsConstructor() {
        Challenge challenge = new Challenge(null, "T", "D", "Cat", Challenge.Difficulty.EASY, 10, "user");
        assertEquals("T", challenge.getTitle());
        assertEquals("D", challenge.getDescription());
        assertEquals("Cat", challenge.getCategory());
        assertEquals(Challenge.Difficulty.EASY, challenge.getDifficulty());
        assertEquals(10, challenge.getPoints());
        assertEquals("user", challenge.getCreatedBy());
    }

    @Test
    void testChallengeValidationSuccess() {
        Challenge challenge = new Challenge(null, "Valid Title", "Desc", "Cat", Challenge.Difficulty.HARD, 100, "user");
        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        assertEquals(0, violations.size());
    }

    @Test
    void testChallengeValidationFail_TitleBlank() {
        Challenge challenge = new Challenge(null, "", "Desc", "Cat", Challenge.Difficulty.MEDIUM, 10, "user");
        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void testChallengeValidationFail_CategoryBlank() {
        Challenge challenge = new Challenge(null, "Title", "Desc", "", Challenge.Difficulty.MEDIUM, 10, "user");
        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("category")));
    }

    @Test
    void testChallengeValidationFail_DifficultyNull() {
        Challenge challenge = new Challenge(null, "Title", "Desc", "Cat", null, 10, "user");
        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("difficulty")));
    }

    @Test
    void testChallengeValidationFail_PointsNotPositive() {
        Challenge challenge = new Challenge(null, "Title", "Desc", "Cat", Challenge.Difficulty.EASY, -5, "user");
        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("points")));
    }

    @Test
    void testChallengeValidationFail_CreatedByBlank() {
        Challenge challenge = new Challenge(null, "Title", "Desc", "Cat", Challenge.Difficulty.EASY, 10, "");
        Set<ConstraintViolation<Challenge>> violations = validator.validate(challenge);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("createdBy")));
    }

    @Test
    void testChallengeEnumValues() {
        assertEquals(3, Challenge.Difficulty.values().length);
        assertEquals(Challenge.Difficulty.EASY, Challenge.Difficulty.valueOf("EASY"));
    }

    // ================================
    // JPA REPOSITORY TESTS
    // ================================

    @Test
    void testSaveChallengeJpa() {
        Challenge challenge = new Challenge(null, "T1", "Desc1", "Fitness", Challenge.Difficulty.MEDIUM, 20, "user1");
        Challenge saved = challengeRepository.save(challenge);

        assertNotNull(saved.getId());
        assertEquals("T1", saved.getTitle());
    }

    @Test
    void testFindChallengeByIdJpa() {
        Challenge challenge = new Challenge(null, "T2", "Desc2", "Fitness", Challenge.Difficulty.HARD, 50, "user2");
        Challenge saved = challengeRepository.save(challenge);

        Challenge found = challengeRepository.findById(saved.getId()).orElseThrow();
        assertEquals("T2", found.getTitle());
    }

    @Test
    void testUpdateChallengeJpa() {
        Challenge challenge = new Challenge(null, "T3", "Desc3", "Fitness", Challenge.Difficulty.EASY, 30, "user3");
        Challenge saved = challengeRepository.save(challenge);

        saved.setTitle("T3 Updated");
        saved.setPoints(40);
        Challenge updated = challengeRepository.save(saved);

        assertEquals("T3 Updated", updated.getTitle());
        assertEquals(40, updated.getPoints());
    }

    @Test
    void testDeleteChallengeJpa() {
        Challenge challenge = new Challenge(null, "T4", "Desc4", "Fitness", Challenge.Difficulty.MEDIUM, 10, "user4");
        Challenge saved = challengeRepository.save(challenge);

        UUID id = saved.getId();
        challengeRepository.deleteById(id);

        assertTrue(challengeRepository.findById(id).isEmpty());
    }
}
