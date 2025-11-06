package challengeme.backend.repository;

import challengeme.backend.exception.BadgeNotFoundException;
import challengeme.backend.model.Badge;
import challengeme.backend.repository.inMemory.InMemoryBadgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BadgeInMemoryRepositoryTests {

    private InMemoryBadgeRepository repository;

    @BeforeEach
    void setup() {
        repository = new InMemoryBadgeRepository();
    }

    @Test
    void testSaveAndFindAll() {
        Badge badge1 = new Badge(UUID.randomUUID(), "Explorer", "Visited 5 locations", "Visit 5 locations");
        Badge badge2 = new Badge(UUID.randomUUID(), "Achiever", "Completed all tasks", "Complete all challenges");

        repository.save(badge1);
        repository.save(badge2);

        List<Badge> badges = repository.findAll();
        assertEquals(2, badges.size());
        assertTrue(badges.contains(badge1));
        assertTrue(badges.contains(badge2));
    }

    @Test
    void testFindById() {
        Badge badge = new Badge(UUID.randomUUID(), "Explorer", "Visited 5 locations", "Visit 5 locations");
        repository.save(badge);

        Badge found = repository.findById(badge.getId());
        assertEquals(badge, found);
    }

    @Test
    void testFindByIdNotFound() {
        UUID id = UUID.randomUUID();
        assertThrows(BadgeNotFoundException.class, () -> repository.findById(id));
    }

    @Test
    void testDelete() {
        Badge badge = new Badge(UUID.randomUUID(), "Explorer", "Visited 5 locations", "Visit 5 locations");
        repository.save(badge);

        repository.delete(badge.getId());

        assertEquals(0, repository.findAll().size());
        assertThrows(BadgeNotFoundException.class, () -> repository.findById(badge.getId()));
    }

    @Test
    void testDeleteNotFound() {
        UUID id = UUID.randomUUID();
        assertThrows(BadgeNotFoundException.class, () -> repository.delete(id));
    }

    @Test
    void testUpdate() {
        Badge badge = new Badge(UUID.randomUUID(), "Explorer", "Visited 5 locations", "Visit 5 locations");
        repository.save(badge);

        Badge updatedBadge = new Badge(badge.getId(), "Explorer Updated", "Visited 10 locations", "Visit 10 locations");
        repository.update(updatedBadge);

        Badge found = repository.findById(badge.getId());
        assertEquals("Explorer Updated", found.getName());
        assertEquals("Visited 10 locations", found.getDescription());
        assertEquals("Visit 10 locations", found.getCriteria());
    }

    @Test
    void testUpdateNotFound() {
        UUID id = UUID.randomUUID();
        Badge badge = new Badge(id, "NonExistent", "Does not exist", "No criteria");

        assertThrows(BadgeNotFoundException.class, () -> repository.update(badge));
    }
}
