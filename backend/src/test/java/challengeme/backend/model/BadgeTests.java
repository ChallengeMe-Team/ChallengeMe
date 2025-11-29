package challengeme.backend.model;

import challengeme.backend.repository.BadgeRepository;
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
public class BadgeTests {

    private static Validator validator;

    @Autowired
    private BadgeRepository badgeRepository;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ================================
    // MODEL / VALIDATION TESTS
    // ================================

    @Test
    void testBadgeGettersSetters() {
        Badge badge = new Badge();
        badge.setName("Champion");
        badge.setDescription("Awarded for winning 10 challenges");
        badge.setCriteria("Win 10 challenges");

        assertEquals("Champion", badge.getName());
        assertEquals("Awarded for winning 10 challenges", badge.getDescription());
        assertEquals("Win 10 challenges", badge.getCriteria());
    }

    @Test
    void testBadgeAllArgsConstructor() {
        Badge badge = new Badge(UUID.randomUUID(), "Achiever", "Completed all tasks", "Complete all challenges");

        assertEquals("Achiever", badge.getName());
        assertEquals("Completed all tasks", badge.getDescription());
        assertEquals("Complete all challenges", badge.getCriteria());
    }

    @Test
    void testBadgeNoArgsConstructor() {
        Badge badge = new Badge();
        assertNull(badge.getId());
        assertNull(badge.getName());
        assertNull(badge.getDescription());
        assertNull(badge.getCriteria());
    }

    @Test
    void testBadgeValidationSuccess() {
        Badge badge = new Badge(UUID.randomUUID(), "Explorer", "Visited 5 new locations", "Visit 5 locations");
        Set<ConstraintViolation<Badge>> violations = validator.validate(badge);
        assertEquals(0, violations.size());
    }

    @Test
    void testBadgeValidationFail_NameBlank() {
        Badge badge = new Badge(UUID.randomUUID(), "", "Valid description", "Criteria text");
        Set<ConstraintViolation<Badge>> violations = validator.validate(badge);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testBadgeValidationFail_DescriptionBlank() {
        Badge badge = new Badge(UUID.randomUUID(), "Valid name", "", "Criteria text");
        Set<ConstraintViolation<Badge>> violations = validator.validate(badge);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void testBadgeValidationFail_BothBlank() {
        Badge badge = new Badge(UUID.randomUUID(), "", "", "Any criteria");
        Set<ConstraintViolation<Badge>> violations = validator.validate(badge);

        assertEquals(2, violations.size());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }
}
