package challengeme.backend.repository;

import challengeme.backend.model.Notification;
import challengeme.backend.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationJPARepositoryTests {

    @Autowired
    private NotificationRepository notificationRepository;

    private UUID userA;
    private UUID userB;

    @BeforeEach
    void setup() {
        userA = UUID.randomUUID();
        userB = UUID.randomUUID();
    }

    private Notification createNotification(UUID userId, String message, NotificationType type) {
        return new Notification(null, userId, message, type, LocalDateTime.now(), false);
    }

    @Test
    void testSaveCreateAndFindById() {
        Notification n = createNotification(userA, "Test message", NotificationType.SYSTEM);
        Notification saved = notificationRepository.save(n);

        // Verificare findById existent
        Notification found = notificationRepository.findById(saved.getId()).orElseThrow();
        assertEquals(saved.getId(), found.getId());
        assertEquals("Test message", found.getMessage());

        // Verificare findById inexistent
        assertTrue(notificationRepository.findById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void testSaveUpdate() {
        Notification n = createNotification(userA, "Original", NotificationType.SYSTEM);
        Notification saved = notificationRepository.save(n);

        // Actualizare mesaj și tip
        saved.setMessage("Updated");
        saved.setType(NotificationType.CHALLENGE);
        Notification updated = notificationRepository.save(saved);

        Notification found = notificationRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Updated", found.getMessage());
        assertEquals(NotificationType.CHALLENGE, found.getType());
    }

    @Test
    void testFindByUserId() {
        Notification n1 = createNotification(userA, "Msg1", NotificationType.SYSTEM);
        Notification n2 = createNotification(userA, "Msg2", NotificationType.CHALLENGE);
        Notification n3 = createNotification(userB, "Other", NotificationType.SYSTEM);

        notificationRepository.save(n1);
        notificationRepository.save(n2);
        notificationRepository.save(n3);

        List<Notification> userANotifications = notificationRepository.findByUserId(userA);
        assertEquals(2, userANotifications.size());

        List<Notification> userBNotifications = notificationRepository.findByUserId(userB);
        assertEquals(1, userBNotifications.size());

        List<Notification> userCNotifications = notificationRepository.findByUserId(UUID.randomUUID());
        assertTrue(userCNotifications.isEmpty());
    }

    @Test
    void testDeleteById() {
        Notification n = createNotification(userA, "To delete", NotificationType.SYSTEM);
        Notification saved = notificationRepository.save(n);

        assertTrue(notificationRepository.existsById(saved.getId()));

        notificationRepository.deleteById(saved.getId());
        assertFalse(notificationRepository.existsById(saved.getId()));
        assertTrue(notificationRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    void testExistsById() {
        Notification n = createNotification(userA, "Check exists", NotificationType.SYSTEM);
        Notification saved = notificationRepository.save(n);

        assertTrue(notificationRepository.existsById(saved.getId()));
        assertFalse(notificationRepository.existsById(UUID.randomUUID()));
    }

    @Test
    void testFindAllEmptyAndNonEmpty() {
        List<Notification> empty = notificationRepository.findAll();
        assertTrue(empty.isEmpty());

        notificationRepository.save(createNotification(userA, "N1", NotificationType.SYSTEM));
        List<Notification> all = notificationRepository.findAll();
        assertEquals(1, all.size());
    }
}
