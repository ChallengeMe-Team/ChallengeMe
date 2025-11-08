package challengeme.backend.repository;

import challengeme.backend.model.Leaderboard;
import challengeme.backend.model.User;
import challengeme.backend.repository.inMemory.InMemoryLeaderboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardInMemoryRepositoryTests {

    private InMemoryLeaderboardRepository repo;

    private User user(String name) {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setUsername(name);
        return u;
    }

    @BeforeEach
    void setup() {
        repo = new InMemoryLeaderboardRepository();
    }

    @Test
    void save_assigns_id_for_new_and_persists() {
        Leaderboard e = new Leaderboard(null, user("ana"), 100);
        Leaderboard saved = repo.save(e);

        assertNotNull(saved.getId());
        assertTrue(repo.existsById(saved.getId()));
    }

    @Test
    void find_update_delete_work() {
        Leaderboard e = repo.save(new Leaderboard(null, user("mihai"), 50));

        // find
        var read = repo.findById(e.getId()).orElseThrow();
        assertEquals(50, read.getTotalPoints());

        // update
        read.setTotalPoints(200);
        repo.save(read);
        assertEquals(200, repo.findById(e.getId()).orElseThrow().getTotalPoints());

        // delete
        repo.deleteById(e.getId());
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void findAll_returns_copy_not_backed_by_storage() {
        repo.save(new Leaderboard(null, user("a"), 1));
        var list = repo.findAll();
        int sizeBefore = list.size();
        list.clear();
        assertEquals(sizeBefore, repo.findAll().size());
    }
}

