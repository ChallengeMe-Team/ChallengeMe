package challengeme.backend.dto.request.create;

import challengeme.backend.model.NotificationType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationCreateRequestTests {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidNotification() {
        NotificationCreateRequest dto = new NotificationCreateRequest(UUID.randomUUID(), "Msg", NotificationType.SYSTEM);
        Set<ConstraintViolation<NotificationCreateRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidNotification() {
        NotificationCreateRequest dto = new NotificationCreateRequest(null, "", null);
        Set<ConstraintViolation<NotificationCreateRequest>> violations = validator.validate(dto);
        assertEquals(3, violations.size());
    }
}
