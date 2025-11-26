package challengeme.backend.dto.request.create;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BadgeCreateRequestTests {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCreate() {
        BadgeCreateRequest dto = new BadgeCreateRequest("Gold", "Top performer", "Complete 10 challenges");
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void testInvalidCreate() {
        BadgeCreateRequest dto = new BadgeCreateRequest("", "", null);
        Set<ConstraintViolation<BadgeCreateRequest>> violations = validator.validate(dto);
        assertEquals(2, violations.size());
    }
}
