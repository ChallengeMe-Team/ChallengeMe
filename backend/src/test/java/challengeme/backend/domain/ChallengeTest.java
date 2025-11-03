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
        Challenge challenge = new Challenge(
                "Test Challenge",
                "Test Description",
                "Fitness",
                Challenge.Difficulty.EASY,
                100,
                "user123"
        );

        assertNotNull(challenge.getId());
        assertEquals("Test Challenge", challenge.getTitle());
        assertEquals(Challenge.Difficulty.EASY, challenge.getDifficulty());
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
}