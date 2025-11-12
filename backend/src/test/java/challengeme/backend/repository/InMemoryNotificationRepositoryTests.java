package challengeme.backend.repository;

import challengeme.backend.model.Notification;
import challengeme.backend.model.NotificationType;
import challengeme.backend.repository.inMemory.InMemoryNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FIȘIER MODIFICAT
 * Rescris complet pentru a testa noua logică a repository-ului bazată pe List, nu pe Map.
 */
class InMemoryNotificationRepositoryTests {

    private InMemoryNotificationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNotificationRepository();
    }

    private Notification createTestNotification(UUID userId) {
        return new Notification(UUID.randomUUID(), userId, "Test", NotificationType.SYSTEM, null, false);
    }

    @Test
    void testSave_CreateNew() {
        Notification notification = createTestNotification(UUID.randomUUID());
        Notification saved = repository.save(notification);

        assertEquals(notification, saved);
        assertEquals(1, repository.findAll().size());
        assertTrue(repository.existsById(notification.getId()));
    }

    @Test
    void testSave_UpdateExisting() {
        Notification notification = createTestNotification(UUID.randomUUID());
        repository.save(notification); // Salvare inițială

        notification.setMessage("Updated Message");
        Notification updated = repository.save(notification); // Salvarea actualizării

        assertEquals(1, repository.findAll().size());
        assertEquals("Updated Message", updated.getMessage());
        // Verificăm că elementul din "baza de date" este cel actualizat
        assertEquals(updated.getMessage(), repository.findById(notification.getId()).get().getMessage());
    }

    @Test
    void testFindById() {
        Notification notification = createTestNotification(UUID.randomUUID());
        repository.save(notification);

        Optional<Notification> found = repository.findById(notification.getId());
        assertTrue(found.isPresent());
        assertEquals(notification.getId(), found.get().getId());

        Optional<Notification> notFound = repository.findById(UUID.randomUUID());
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testFindByUserId() {
        UUID userA = UUID.randomUUID();
        UUID userB = UUID.randomUUID();

        repository.save(createTestNotification(userA));
        repository.save(createTestNotification(userA));
        repository.save(createTestNotification(userB));

        List<Notification> userANotifications = repository.findByUserId(userA);
        assertEquals(2, userANotifications.size());

        List<Notification> userBNotifications = repository.findByUserId(userB);
        assertEquals(1, userBNotifications.size());

        List<Notification> userCNotifications = repository.findByUserId(UUID.randomUUID());
        assertEquals(0, userCNotifications.size());
    }

    @Test
    void testDeleteById() {
        Notification n1 = createTestNotification(UUID.randomUUID());
        Notification n2 = createTestNotification(UUID.randomUUID());
        repository.save(n1);
        repository.save(n2);

        assertEquals(2, repository.findAll().size());

        repository.deleteById(n1.getId());

        assertEquals(1, repository.findAll().size());
        assertFalse(repository.existsById(n1.getId()));
        assertTrue(repository.existsById(n2.getId()));
    }

    @Test
    void testExistsById() {
        Notification notification = createTestNotification(UUID.randomUUID());
        repository.save(notification);

        assertTrue(repository.existsById(notification.getId()));
        assertFalse(repository.existsById(UUID.randomUUID()));
    }
}