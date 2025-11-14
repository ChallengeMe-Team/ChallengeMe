package challengeme.backend.repository;

import challengeme.backend.model.Leaderboard;
import challengeme.backend.model.User;
import challengeme.backend.repository.inMemory.InMemoryLeaderboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class LeaderboardInMemoryRepositoryTests {

    private InMemoryLeaderboardRepository repo;

    @BeforeEach
    void setup() {
        repo = new InMemoryLeaderboardRepository();
    }

    private static User user(String name) {

        return new User(name, name + "@email.com", "secret12", 0);
    }

    @Test
    void save_assigns_id_for_new_and_persists() {
        Leaderboard e = new Leaderboard();
        e.setUser(user("ana"));
        e.setTotalPoints(100);

        Leaderboard saved = repo.save(e);

        assertNotNull(saved.getId());

        assertDoesNotThrow(() -> repo.findById(saved.getId()));
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void find_update_delete_work() {
        Leaderboard e = new Leaderboard();
        e.setUser(user("mihai"));
        e.setTotalPoints(50);
        repo.save(e);


        Leaderboard read = repo.findById(e.getId());
        assertEquals(50, read.getTotalPoints());


        read.setTotalPoints(200);
        repo.update(read);
        assertEquals(200, repo.findById(e.getId()).getTotalPoints());


        repo.delete(e.getId());
        assertTrue(repo.findAll().isEmpty());
    }
}
