package challengeme.backend.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FIȘIER MODIFICAT
 * Testează modelul Notification, inclusiv noile validări @NotBlank/@NotNull.
 */
class NotificationTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Inițializează validatorul
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNotificationModelValid() {
        Notification notification = new Notification(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Valid message",
                NotificationType.SYSTEM,
                null,
                false
        );

        // Verifică dacă nu există încălcări ale regulilor de validare
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertTrue(violations.isEmpty(), "Notification ar trebui sa fie valida");
    }

    @Test
    void testNotificationMessageBlank() {
        Notification notification = new Notification(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "", // Mesaj invalid (blank)
                NotificationType.SYSTEM,
                null,
                false
        );

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty(), "Ar trebui sa existe o incalcare a validarii");
        ConstraintViolation<Notification> violation = violations.iterator().next();
        assertEquals("Message cannot be blank", violation.getMessage());
        assertEquals("message", violation.getPropertyPath().toString());
    }

    @Test
    void testNotificationTypeNull() {
        Notification notification = new Notification(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Valid message",
                null, // Tip invalid (null)
                null,
                false
        );

        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        assertFalse(violations.isEmpty(), "Ar trebui sa existe o incalcare a validarii");
        ConstraintViolation<Notification> violation = violations.iterator().next();
        assertEquals("NotificationType cannot be null", violation.getMessage());
        assertEquals("type", violation.getPropertyPath().toString());
    }
}