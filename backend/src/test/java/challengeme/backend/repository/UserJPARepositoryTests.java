package challengeme.backend.repository;

import challengeme.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserJPARepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private User userA;
    private User userB;

    @BeforeEach
    void setup() {
        userA = new User(null, "Ana", "ana@email.com", "pass123", 10);
        userB = new User(null, "Ion", "ion@email.com", "pass456", 5);
    }

    @Test
    void testSaveAndFindById() {
        User saved = userRepository.save(userA);
        User found = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Ana", found.getUsername());
        assertEquals("ana@email.com", found.getEmail());
        assertEquals("pass123", found.getPassword());
        assertEquals(10, found.getPoints());

        assertTrue(userRepository.findById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void testFindAllEmptyAndNonEmpty() {
        List<User> emptyList = userRepository.findAll();
        assertTrue(emptyList.isEmpty());

        userRepository.save(userA);
        userRepository.save(userB);
        List<User> all = userRepository.findAll();
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(u -> u.getUsername().equals("Ana")));
        assertTrue(all.stream().anyMatch(u -> u.getUsername().equals("Ion")));
    }

    @Test
    void testUpdateExistingUser() {
        User saved = userRepository.save(userA);

        saved.setUsername("AnaUpdated");
        saved.setEmail("ana_updated@email.com");
        saved.setPassword("newpass123");
        saved.setPoints(20);
        userRepository.save(saved);

        User updated = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals("AnaUpdated", updated.getUsername());
        assertEquals("ana_updated@email.com", updated.getEmail());
        assertEquals("newpass123", updated.getPassword());
        assertEquals(20, updated.getPoints());
    }

    @Test
    void testDeleteById() {
        User saved = userRepository.save(userA);

        assertTrue(userRepository.findById(saved.getId()).isPresent());
        userRepository.deleteById(saved.getId());
        assertTrue(userRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    void testMultipleUsers() {
        userRepository.save(userA);
        userRepository.save(userB);

        List<User> all = userRepository.findAll();
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(u -> u.getUsername().equals("Ana")));
        assertTrue(all.stream().anyMatch(u -> u.getUsername().equals("Ion")));

        userRepository.deleteById(userA.getId());
        all = userRepository.findAll();
        assertEquals(1, all.size());
        assertEquals("Ion", all.get(0).getUsername());

        userRepository.deleteById(userB.getId());
        all = userRepository.findAll();
        assertTrue(all.isEmpty());
    }
}
