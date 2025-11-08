package challengeme.backend.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Teste unitare pentru clasa Model Notification.
 * Verifica constructorii, getter-ii, setter-ii si metodele
 * generate de Lombok (@Data).
 */
class NotificationTests {

    @Test
    void testNoArgsConstructorAndSetters() {
        Notification notification = new Notification();

        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String message = "Test Message";
        NotificationType type = NotificationType.SYSTEM;
        LocalDateTime timestamp = LocalDateTime.now();
        boolean isRead = true;

        notification.setId(id);
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setTimestamp(timestamp);
        notification.setRead(isRead);

        assertEquals(id, notification.getId());
        assertEquals(userId, notification.getUserId());
        assertEquals(message, notification.getMessage());
        assertEquals(type, notification.getType());
        assertEquals(timestamp, notification.getTimestamp());
        assertEquals(isRead, notification.isRead());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String message = "All Args Message";
        NotificationType type = NotificationType.CHALLENGE;
        LocalDateTime timestamp = LocalDateTime.now();
        boolean isRead = false;

        Notification notification = new Notification(id, userId, message, type, timestamp, isRead);

        assertEquals(id, notification.getId());
        assertEquals(userId, notification.getUserId());
        assertEquals(message, notification.getMessage());
        assertEquals(type, notification.getType());
        assertEquals(timestamp, notification.getTimestamp());
        assertEquals(isRead, notification.isRead());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String message = "Test Message";
        NotificationType type = NotificationType.BADGE;
        LocalDateTime timestamp = LocalDateTime.now();

        Notification n1 = new Notification(id, userId, message, type, timestamp, false);
        Notification n2 = new Notification(id, userId, message, type, timestamp, false);
        Notification n3 = new Notification(id, userId, "Different Message", type, timestamp, false);

        // Teste pentru equals
        assertEquals(n1, n2);
        assertNotEquals(n1, n3);

        // Teste pentru hashCode
        assertEquals(n1.hashCode(), n2.hashCode());
        assertNotEquals(n1.hashCode(), n3.hashCode());
    }

    @Test
    void testToString() {
        Notification notification = new Notification();
        notification.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        notification.setMessage("Test");

        assertTrue(notification.toString().contains("123e4567-e89b-12d3-a456-426614174000"));
        assertTrue(notification.toString().contains("Test"));
    }
}