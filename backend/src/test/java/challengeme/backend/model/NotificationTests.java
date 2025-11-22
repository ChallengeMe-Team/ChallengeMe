package challengeme.backend.model;

import challengeme.backend.repository.NotificationRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationTests {

    private static Validator validator;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==============================
    // VALIDATION TESTS
    // ==============================
    @Test
    void testValidationSuccessWithAllTypes() {
        for (NotificationType type : NotificationType.values()) {
            Notification notification = new Notification(
                    null,
                    UUID.randomUUID(),
                    "Valid message",
                    type,
                    LocalDateTime.now(),
                    false
            );
            Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
            assertEquals(0, violations.size(), "Notification should be valid for type " + type);
        }
    }

    @Test
    void testValidationFail_UserIdNull() {
        Notification notification = new Notification(
                null,
                null,
                "Valid message",
                NotificationType.SYSTEM,
                LocalDateTime.now(),
                false
        );

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userId")));
    }

    @Test
    void testValidationFail_MessageBlank() {
        Notification notification = new Notification(
                null,
                UUID.randomUUID(),
                "",
                NotificationType.SYSTEM,
                LocalDateTime.now(),
                false
        );

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("message")));
    }

    @Test
    void testValidationFail_TypeNull() {
        Notification notification = new Notification(
                null,
                UUID.randomUUID(),
                "Valid message",
                null,
                LocalDateTime.now(),
                false
        );

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("type")));
    }

}
