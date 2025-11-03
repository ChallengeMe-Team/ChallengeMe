package challengeme.backend.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserBadgeTests {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testUserBadgeGettersSetters() {
        UUID id = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com", "secret123", 10);
        Badge badge = new Badge(UUID.randomUUID(), "Gold", "Top performer badge", "Complete 10 challenges");

        UserBadge userBadge = new UserBadge();
        userBadge.setId(id);
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setDateAwarded(LocalDate.of(2025, 1, 1));

        assertEquals(id, userBadge.getId());
        assertEquals(user, userBadge.getUser());
        assertEquals(badge, userBadge.getBadge());
        assertEquals(LocalDate.of(2025, 1, 1), userBadge.getDateAwarded());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "Ion", "ion@email.com", "pass1234", 5);
        Badge badge = new Badge(UUID.randomUUID(), "Silver", "Runner-up badge", "Achieve 5 challenges");
        LocalDate date = LocalDate.of(2024, 12, 31);

        UserBadge userBadge = new UserBadge(id, user, badge, date);

        assertEquals(id, userBadge.getId());
        assertEquals(user, userBadge.getUser());
        assertEquals(badge, userBadge.getBadge());
        assertEquals(date, userBadge.getDateAwarded());
    }

    @Test
    void testNoArgsConstructorAndDefaultDate() {
        UserBadge userBadge = new UserBadge();
        assertNotNull(userBadge.getDateAwarded(), "Date should be initialized by default");
        assertTrue(userBadge.getDateAwarded().isBefore(LocalDate.now().plusDays(1)));
    }

    @Test
    void testValidationSuccess() {
        User user = new User("Ana", "ana@email.com", "secret123", 15);
        Badge badge = new Badge(UUID.randomUUID(), "Gold", "Top performer badge", "Complete 10 challenges");
        UserBadge userBadge = new UserBadge(UUID.randomUUID(), user, badge, LocalDate.now());

        Set<ConstraintViolation<UserBadge>> violations = validator.validate(userBadge);
        assertEquals(0, violations.size());
    }

    @Test
    void testValidationFail_UserNull() {
        Badge badge = new Badge(UUID.randomUUID(), "Gold", "Top performer badge", "Complete 10 challenges");
        UserBadge userBadge = new UserBadge(UUID.randomUUID(), null, badge, LocalDate.now());

        Set<ConstraintViolation<UserBadge>> violations = validator.validate(userBadge);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("user")));
    }

    @Test
    void testValidationFail_BadgeNull() {
        User user = new User("Ana", "ana@email.com", "secret123", 10);
        UserBadge userBadge = new UserBadge(UUID.randomUUID(), user, null, LocalDate.now());

        Set<ConstraintViolation<UserBadge>> violations = validator.validate(userBadge);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("badge")));
    }
}
