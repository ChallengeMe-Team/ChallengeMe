package challengeme.backend.repository;

import challengeme.backend.exception.EntityNotFoundException;
import challengeme.backend.model.*;
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
        UserBadge ub = new UserBadge(UUID.randomUUID(),
                new User(UUID.randomUUID(), "Ana", "ana@email.com"),
                new Badge(UUID.randomUUID(), "Gold", "desc"),
                LocalDate.now());

        repository.create(ub);
        UserBadge found = repository.getUserBadge(ub.getId());

        assertEquals(ub, found);
    }

    @Test
    void testUpdate() {
        UserBadge ub = new UserBadge(UUID.randomUUID(), new User(), new Badge(), LocalDate.now());
        repository.create(ub);

        Badge newBadge = new Badge(UUID.randomUUID(), "Silver", "Updated");
        UserBadge updated = new UserBadge(ub.getId(), new User(), newBadge, LocalDate.now());
        repository.update(updated);

        assertEquals("Silver", repository.getUserBadge(ub.getId()).getBadge().getName());
    }

    @Test
    void testDelete() {
        UserBadge ub = new UserBadge(UUID.randomUUID(), new User(), new Badge(), LocalDate.now());
        repository.create(ub);
        repository.delete(ub.getId());

        assertThrows(EntityNotFoundException.class, () -> repository.getUserBadge(ub.getId()));
    }

    @Test
    void testDeleteNonExistingThrows() {
        assertThrows(EntityNotFoundException.class, () -> repository.delete(UUID.randomUUID()));
    }
}
