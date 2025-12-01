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
        UserCreateRequest dto = new UserCreateRequest("Ana", "ana@email.com", "Password_123");
        Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidUserCreateRequest() {
        UserCreateRequest dto = new UserCreateRequest("", "not-an-email", "");
        Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(dto);
        assertEquals(5, violations.size());
    }

    @Test
    void testPasswordValidation_Success() {
        // Parolă validă: 6 chars, Upper, Lower, Digit, Special
        UserCreateRequest request = new UserCreateRequest("user", "test@email.com", "Password_123");

        Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid password should not have violations");
    }

    @Test
    void testPasswordValidation_Failures() {
        // Caz 1: Prea scurtă
        assertPasswordFails("Pa1!", "Short password should fail");

        // Caz 2: Fără majusculă
        assertPasswordFails("pass123!", "No uppercase should fail");

        // Caz 3: Fără minusculă
        assertPasswordFails("PASS123!", "No lowercase should fail");

        assertPasswordFails("PassWord!", "No digit should fail");

        assertPasswordFails("PassWord123", "No special char should fail");
    }

    private void assertPasswordFails(String password, String message) {
        UserCreateRequest request = new UserCreateRequest("user", "test@email.com", password);
        Set<ConstraintViolation<UserCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), message);
    }
}
