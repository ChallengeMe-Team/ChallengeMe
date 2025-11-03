package challengeme.backend.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testUserGettersSetters() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setUsername("Ana");
        user.setEmail("ana@email.com");
        user.setPassword("secret123");
        user.setPoints(10);

        assertEquals(id, user.getId());
        assertEquals("Ana", user.getUsername());
        assertEquals("ana@email.com", user.getEmail());
        assertEquals("secret123", user.getPassword());
        assertEquals(10, user.getPoints());
    }

    @Test
    void testUserAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Ion", "ion@email.com", "pass1234", 5);

        assertEquals(id, user.getId());
        assertEquals("Ion", user.getUsername());
        assertEquals("ion@email.com", user.getEmail());
        assertEquals("pass1234", user.getPassword());
        assertEquals(5, user.getPoints());
    }

    @Test
    void testUserPartialConstructor() {
        User user = new User("Maria", "maria@email.com", "password", null);

        assertNotNull(user.getId());
        assertEquals("Maria", user.getUsername());
        assertEquals("maria@email.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(0, user.getPoints()); // points set to 0 by constructor
    }

    @Test
    void testUserValidationSuccess() {
        User user = new User("Ana", "ana@email.com", "secret123", 10);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    void testUserValidationFail_UsernameBlank() {
        User user = new User("", "ana@email.com", "secret123", 0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void testUserValidationFail_UsernameTooShort() {
        User user = new User("Al", "ana@email.com", "secret123", 0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void testUserValidationFail_EmailInvalid() {
        User user = new User("Ana", "not-an-email", "secret123", 0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testUserValidationFail_PasswordBlank() {
        User user = new User("Ana", "ana@email.com", "", 0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void testUserValidationFail_PasswordTooShort() {
        User user = new User("Ana", "ana@email.com", "123", 0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
}
