package challengeme.backend.repository;

import challengeme.backend.exception.EntityNotFoundException;
import challengeme.backend.model.Badge;
import challengeme.backend.model.User;
import challengeme.backend.model.UserBadge;
import challengeme.backend.repository.inMemory.InMemoryRepositoryUserBadge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryRepositoryUserBadgeTests {

    private InMemoryRepositoryUserBadge repository;

    @BeforeEach
    void setup() {
        repository = new InMemoryRepositoryUserBadge();
    }

    @Test
    void testCreateAndFind() {
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com", "secret123", 10);
        Badge badge = new Badge(UUID.randomUUID(), "Gold", "Top performer badge", "Complete 10 challenges");
        UserBadge ub = new UserBadge(UUID.randomUUID(), user, badge, LocalDate.now());

        repository.create(ub);
        UserBadge found = repository.getUserBadge(ub.getId());

        assertEquals(ub, found);
    }

    @Test
    void testUpdate() {
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com", "secret123", 10);
        Badge badge = new Badge(UUID.randomUUID(), "Gold", "Top performer badge", "Complete 10 challenges");
        UserBadge ub = new UserBadge(UUID.randomUUID(), user, badge, LocalDate.now());
        repository.create(ub);

        // Update badge
        Badge newBadge = new Badge(UUID.randomUUID(), "Silver", "Updated badge", "Achieve 20 challenges");
        User updatedUser = new User(UUID.randomUUID(), "Ion", "ion@email.com", "newpass123", 15);
        UserBadge updated = new UserBadge(ub.getId(), updatedUser, newBadge, LocalDate.now());
        repository.update(updated);

        UserBadge result = repository.getUserBadge(ub.getId());
        assertEquals("Silver", result.getBadge().getName());
        assertEquals("Ion", result.getUser().getUsername());
        assertEquals("Achieve 20 challenges", result.getBadge().getCriteria());
    }

    @Test
    void testDelete() {
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com", "secret123", 10);
        Badge badge = new Badge(UUID.randomUUID(), "Gold", "Top performer badge", "Complete 10 challenges");
        UserBadge ub = new UserBadge(UUID.randomUUID(), user, badge, LocalDate.now());

        repository.create(ub);
        repository.delete(ub.getId());

        assertThrows(EntityNotFoundException.class, () -> repository.getUserBadge(ub.getId()));
    }

    @Test
    void testDeleteNonExistingThrows() {
        assertThrows(EntityNotFoundException.class, () -> repository.delete(UUID.randomUUID()));
    }
}
