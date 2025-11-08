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
 * Teste unitare pentru InMemoryNotificationRepository.
 * Aceste teste ruleaza direct logica repository-ului
 * fara a folosi mock-uri.
 */
class InMemoryNotificationRepositoryTests {

    private InMemoryNotificationRepository repository;

    @BeforeEach
    void setUp() {
        // Cream o noua instanta a repository-ului inainte de fiecare test
        // pentru a ne asigura ca testele sunt izolate (baza de date e goala)
        repository = new InMemoryNotificationRepository();
    }

    @Test
    void testSave_CreateNewNotification() {
        Notification notification = new Notification();
        notification.setUserId(UUID.randomUUID());
        notification.setMessage("New Notification");
        notification.setType(NotificationType.SYSTEM);

        Notification saved = repository.save(notification);

        assertNotNull(saved.getId());
        assertNotNull(saved.getTimestamp());
        assertFalse(saved.isRead());
        assertEquals("New Notification", saved.getMessage());

        // Verificam si daca exista in "baza de date"
        Optional<Notification> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void testSave_UpdateExistingNotification() throws InterruptedException {
        Notification notification = new Notification();
        notification.setUserId(UUID.randomUUID());
        notification.setMessage("Original Message");
        notification.setType(NotificationType.CHALLENGE);

        Notification saved = repository.save(notification);
        var originalTimestamp = saved.getTimestamp();

        // Asiguram o diferenta de timp
        Thread.sleep(10);

        saved.setRead(true);
        saved.setMessage("Updated Message");
        Notification updated = repository.save(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated Message", updated.getMessage());
        assertTrue(updated.isRead());
        // Timestamp-ul ar trebui sa fie actualizat la salvare
        assertNotEquals(originalTimestamp, updated.getTimestamp());
    }

    @Test
    void testFindById_Found() {
        Notification saved = createAndSaveTestNotification(UUID.randomUUID());
        Optional<Notification> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Notification> found = repository.findById(UUID.randomUUID());
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        createAndSaveTestNotification(UUID.randomUUID());
        createAndSaveTestNotification(UUID.randomUUID());

        List<Notification> all = repository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testFindByUserId() {
        UUID userA = UUID.randomUUID();
        UUID userB = UUID.randomUUID();

        createAndSaveTestNotification(userA);
        createAndSaveTestNotification(userA);
        createAndSaveTestNotification(userB);

        List<Notification> userANotifications = repository.findByUserId(userA);
        List<Notification> userBNotifications = repository.findByUserId(userB);
        List<Notification> userCNotifications = repository.findByUserId(UUID.randomUUID());

        assertEquals(2, userANotifications.size());
        assertEquals(1, userBNotifications.size());
        assertEquals(0, userCNotifications.size());
    }

    @Test
    void testDeleteById() {
        Notification saved = createAndSaveTestNotification(UUID.randomUUID());
        UUID id = saved.getId();

        assertTrue(repository.existsById(id));
        repository.deleteById(id);
        assertFalse(repository.existsById(id));
        assertFalse(repository.findById(id).isPresent());
    }

    @Test
    void testExistsById() {
        Notification saved = createAndSaveTestNotification(UUID.randomUUID());
        assertTrue(repository.existsById(saved.getId()));
        assertFalse(repository.existsById(UUID.randomUUID()));
    }

    // Metoda helper pentru a crea si salva rapid o notificare
    private Notification createAndSaveTestNotification(UUID userId) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage("Test");
        n.setType(NotificationType.BADGE);
        return repository.save(n);
    }
}