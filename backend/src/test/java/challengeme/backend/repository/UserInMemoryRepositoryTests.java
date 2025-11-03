package challengeme.backend.repository;

import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.model.User;
import challengeme.backend.repository.inMemory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserInMemoryRepositoryTests {

    private InMemoryUserRepository repository;

    @BeforeEach
    void setup() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void testSaveAndFindAll() {
        User user1 = new User("Ana", "ana@email.com", "password123", 10);
        User user2 = new User("Ion", "ion@email.com", "pass4567", 5);

        repository.save(user1);
        repository.save(user2);

        List<User> users = repository.findAll();
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void testFindById() {
        User user = new User("Ana", "ana@email.com", "password123", 10);
        repository.save(user);

        User found = repository.findById(user.getId());
        assertEquals(user, found);
    }

    @Test
    void testFindByIdNotFound() {
        UUID id = UUID.randomUUID();
        assertThrows(UserNotFoundException.class, () -> repository.findById(id));
    }

    @Test
    void testDelete() {
        User user = new User("Ana", "ana@email.com", "password123", 10);
        repository.save(user);

        repository.delete(user.getId());

        assertEquals(0, repository.findAll().size());
        assertThrows(UserNotFoundException.class, () -> repository.findById(user.getId()));
    }

    @Test
    void testDeleteNotFound() {
        UUID id = UUID.randomUUID();
        assertThrows(UserNotFoundException.class, () -> repository.delete(id));
    }

    @Test
    void testUpdate() {
        User user = new User("Ana", "ana@email.com", "password123", 10);
        repository.save(user);

        User updatedUser = new User(user.getId(), "AnaUpdated", "ana_updated@email.com", "newpass123", 20);
        repository.update(updatedUser);

        User found = repository.findById(user.getId());
        assertEquals("AnaUpdated", found.getUsername());
        assertEquals("ana_updated@email.com", found.getEmail());
        assertEquals("newpass123", found.getPassword());
        assertEquals(20, found.getPoints());
    }

    @Test
    void testUpdateNotFound() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "AnaUpdated", "ana_updated@email.com", "newpass123", 20);

        assertThrows(UserNotFoundException.class, () -> repository.update(user));
    }
}
