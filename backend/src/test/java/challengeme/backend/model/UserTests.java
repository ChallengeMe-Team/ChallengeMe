package challengeme.backend.model;

import challengeme.backend.repository.UserRepository;
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
public class UserTests {

    private static Validator validator;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ================================
    // VALIDATION + GETTER / SETTER TESTS
    // ================================

    @Test
    void testUserGettersSetters() {
        User user = new User();
        user.setUsername("Ana");
        user.setEmail("ana@email.com");
        user.setPassword("secret123");
        user.setPoints(10);

        assertEquals("Ana", user.getUsername());
        assertEquals("ana@email.com", user.getEmail());
        assertEquals("secret123", user.getPassword());
        assertEquals(10, user.getPoints());
    }

    @Test
    void testUserAllArgsConstructor() {
        User user = new User(null, "Ion", "ion@email.com", "pass1234", 5);

        assertEquals("Ion", user.getUsername());
        assertEquals("ion@email.com", user.getEmail());
        assertEquals("pass1234", user.getPassword());
        assertEquals(5, user.getPoints());
    }

    @Test
    void testUserValidationSuccess() {
        User user = new User();
        user.setUsername("Ana");
        user.setEmail("ana@email.com");
        user.setPassword("secret123");
        user.setPoints(10);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    void testUserValidationFail_UsernameBlank() {
        User user = new User();
        user.setUsername("");
        user.setEmail("ana@email.com");
        user.setPassword("secret123");
        user.setPoints(0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void testUserValidationFail_UsernameTooShort() {
        User user = new User();
        user.setUsername("Al");
        user.setEmail("ana@email.com");
        user.setPassword("secret123");
        user.setPoints(0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void testUserValidationFail_EmailInvalid() {
        User user = new User();
        user.setUsername("Ana");
        user.setEmail("not-an-email");
        user.setPassword("secret123");
        user.setPoints(0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testUserValidationFail_PasswordBlank() {
        User user = new User();
        user.setUsername("Ana");
        user.setEmail("ana@email.com");
        user.setPassword("");
        user.setPoints(0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void testUserValidationFail_PasswordTooShort() {
        User user = new User();
        user.setUsername("Ana");
        user.setEmail("ana@email.com");
        user.setPassword("123");
        user.setPoints(0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    // ================================
    // JPA REPOSITORY TESTS
    // ================================

    @Test
    void testSaveUserJpa() {
        User user = new User();
        user.setUsername("Ana");
        user.setEmail("ana@email.com");
        user.setPassword("secret123");
        user.setPoints(10);

        User saved = userRepository.save(user);

        assertNotNull(saved.getId()); // UUID generat automat
        assertEquals("Ana", saved.getUsername());
        assertEquals(10, saved.getPoints());
    }

    @Test
    void testFindUserByIdJpa() {
        User user = new User();
        user.setUsername("Ion");
        user.setEmail("ion@email.com");
        user.setPassword("pass123");
        user.setPoints(5);

        User saved = userRepository.save(user);

        User found = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Ion", found.getUsername());
    }

    @Test
    void testUpdateUserJpa() {
        User user = new User();
        user.setUsername("Maria");
        user.setEmail("maria@email.com");
        user.setPassword("password");
        user.setPoints(2);

        User saved = userRepository.save(user);

        saved.setPoints(10);
        User updated = userRepository.save(saved);

        assertEquals(10, updated.getPoints());
    }

    @Test
    void testDeleteUserJpa() {
        User user = new User();
        user.setUsername("Alex");
        user.setEmail("alex@email.com");
        user.setPassword("secret123");
        user.setPoints(7);

        User saved = userRepository.save(user);
        UUID id = saved.getId();

        userRepository.deleteById(id);

        assertTrue(userRepository.findById(id).isEmpty());
    }
}
