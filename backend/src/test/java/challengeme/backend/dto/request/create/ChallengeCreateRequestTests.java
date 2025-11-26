package challengeme.backend.dto.request.create;

import challengeme.backend.model.Challenge.Difficulty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeCreateRequestTests {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidChallengeCreateRequest() {
        ChallengeCreateRequest dto = new ChallengeCreateRequest(
                "Title", "Desc", "Category", Difficulty.EASY, 10, "creator"
        );
        Set<ConstraintViolation<ChallengeCreateRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidFields() {
        ChallengeCreateRequest dto = new ChallengeCreateRequest(
                "", "Desc", "", null, -5, ""
        );
        Set<ConstraintViolation<ChallengeCreateRequest>> violations = validator.validate(dto);
        assertEquals(5, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("category")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("difficulty")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("points")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("createdBy")));
    }
}
