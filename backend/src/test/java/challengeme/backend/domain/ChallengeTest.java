package challengeme.backend.domain;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeTest {

    private final Validator validator;

    public ChallengeTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateChallengeWithValidData() {
        Challenge challenge = new Challenge("Test", "Desc", "Cat", Challenge.Difficulty.HARD, 200, "user");
        assertNotNull(challenge.getId());
        assertEquals("Test", challenge.getTitle());
    }

    @Test
    void shouldFailValidationWhenTitleIsBlank() {
        Challenge challenge = new Challenge();
        challenge.setTitle("");
        challenge.setCategory("Fitness");
        challenge.setDifficulty(Challenge.Difficulty.EASY);
        challenge.setPoints(100);
        challenge.setCreatedBy("user123");

        var violations = validator.validate(challenge);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenPointsNotPositive() {
        Challenge challenge = new Challenge("T", "D", "C", Challenge.Difficulty.MEDIUM, -10, "U");
        var violations = validator.validate(challenge);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenCreatedByBlank() {
        Challenge challenge = new Challenge("T", "D", "C", Challenge.Difficulty.MEDIUM, 100, "");
        var violations = validator.validate(challenge);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldHaveWorkingEnumValues() {
        assertEquals(3, Challenge.Difficulty.values().length);
        assertEquals(Challenge.Difficulty.EASY, Challenge.Difficulty.valueOf("EASY"));
    }
}
