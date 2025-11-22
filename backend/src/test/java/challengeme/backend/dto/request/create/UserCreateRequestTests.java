package challengeme.backend.dto.request.create;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserCreateRequestTests {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUserCreateRequest() {
        UserCreateRequest dto = new UserCreateRequest("Ana", "ana@email.com", "secret123");
        Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidUserCreateRequest() {
        UserCreateRequest dto = new UserCreateRequest("", "not-an-email", "");
        Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(dto);
        assertEquals(3, violations.size());
    }
}
