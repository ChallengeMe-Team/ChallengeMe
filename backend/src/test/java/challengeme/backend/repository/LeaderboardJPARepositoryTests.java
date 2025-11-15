package challengeme.backend.repository;

import challengeme.backend.model.Leaderboard;
import challengeme.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LeaderboardJPARepositoryTests {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {

        User u1 = new User();
        u1.setUsername("User1");
        u1.setEmail("u1@email.com");
        u1.setPassword("test_pass_1");
        u1.setPoints(0);

        user1 = userRepository.save(u1);

        User u2 = new User();
        u2.setUsername("User2");
        u2.setEmail("u2@email.com");
        u2.setPassword("test_pass_2");
        u2.setPoints(10);

        user2 = userRepository.save(u2);
    }

    @Test
    void testSaveAndFindAll() {
        Leaderboard lb1 = new Leaderboard();
        lb1.setUser(user1);
        lb1.setTotalPoints(100);
        lb1.setRank(2);

        Leaderboard lb2 = new Leaderboard();
        lb2.setUser(user2);
        lb2.setTotalPoints(50);
        lb2.setRank(1);

        leaderboardRepository.save(lb1);
        leaderboardRepository.save(lb2);

        List<Leaderboard> all = leaderboardRepository.findAll();
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(lb -> lb.getUser().getUsername().equals("User1")));
        assertTrue(all.stream().anyMatch(lb -> lb.getUser().getUsername().equals("User2")));
    }

    @Test
    void testFindById() {
        Leaderboard lb = new Leaderboard();
        lb.setUser(user1);
        lb.setTotalPoints(120);
        lb.setRank(1);
        Leaderboard saved = leaderboardRepository.save(lb);

        Leaderboard found = leaderboardRepository.findById(saved.getId()).orElseThrow();
        assertEquals(120, found.getTotalPoints());
        assertEquals("User1", found.getUser().getUsername());
    }

    @Test
    void testUpdate() {
        Leaderboard lb = new Leaderboard();
        lb.setUser(user1);
        lb.setTotalPoints(50);
        lb.setRank(3);
        Leaderboard saved = leaderboardRepository.save(lb);


        saved.setTotalPoints(200);
        leaderboardRepository.save(saved);

        Leaderboard updated = leaderboardRepository.findById(saved.getId()).orElseThrow();
        assertEquals(200, updated.getTotalPoints());
    }

    @Test
    void testDelete() {
        Leaderboard lb = new Leaderboard();
        lb.setUser(user1);
        lb.setTotalPoints(50);
        lb.setRank(5);
        Leaderboard saved = leaderboardRepository.save(lb);

        leaderboardRepository.deleteById(saved.getId());
        assertTrue(leaderboardRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    void testExistsById() {
        Leaderboard lb = new Leaderboard();
        lb.setUser(user1);
        lb.setTotalPoints(70);
        lb.setRank(1);
        Leaderboard saved = leaderboardRepository.save(lb);

        assertTrue(leaderboardRepository.existsById(saved.getId()));
        assertFalse(leaderboardRepository.existsById(UUID.randomUUID()));
    }

    @Test
    void testFindAllEmpty() {
        List<Leaderboard> all = leaderboardRepository.findAll();
        assertTrue(all.isEmpty());
    }
}