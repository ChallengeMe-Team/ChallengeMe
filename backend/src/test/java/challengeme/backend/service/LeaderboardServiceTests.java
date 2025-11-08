package challengeme.backend.service;

import challengeme.backend.model.Leaderboard;
import challengeme.backend.model.User;
import challengeme.backend.repository.LeaderboardRepository;
import challengeme.backend.repository.UserRepository;
import challengeme.backend.repository.inMemory.InMemoryLeaderboardRepository;
import challengeme.backend.repository.inMemory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class LeaderboardServiceTests {

    private LeaderboardService service;
    private UserRepository userRepo;

    private UUID u1Id, u2Id;

    private User makeUser(String name) {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setUsername(name);
        return u;
    }

    @BeforeEach
    void setup() {
        LeaderboardRepository lbRepo = new InMemoryLeaderboardRepository();
        userRepo = new InMemoryUserRepository();

        User u1 = makeUser("ana"); userRepo.save(u1); u1Id = u1.getId();
        User u2 = makeUser("mihai"); userRepo.save(u2); u2Id = u2.getId();

        service = new LeaderboardService(lbRepo, userRepo);
    }

    private Leaderboard add(UUID userId, int points) {
        return service.create(userId, points);
    }

    @Test
    void create_and_getAll() {
        add(u1Id, 120);
        add(u2Id, 90);
        List<Leaderboard> all = service.getAll();
        assertEquals(2, all.size());
    }

    @Test
    void sorted_desc_and_rank_calculated() {
        add(u1Id, 120);
        add(u2Id, 90);

        var sorted = service.getSortedDescByPoints();
        assertEquals(120, sorted.get(0).getTotalPoints());
        assertEquals(1, sorted.get(0).getRank());
        assertEquals(2, sorted.get(1).getRank());
    }

    @Test
    void update_recomputes_ranks() {
        var e1 = add(u1Id, 100);
        var e2 = add(u2Id, 50);

        service.update(e2.getId(), 200);

        var sorted = service.getSortedDescByPoints();
        assertEquals(e2.getId(), sorted.get(0).getId());
        assertEquals(1, sorted.get(0).getRank());
        assertEquals(2, sorted.get(1).getRank());
    }

    @Test
    void delete_recomputes_ranks() {
        var e1 = add(u1Id, 150);
        var e2 = add(u2Id, 120);

        service.delete(e1.getId());
        var sorted = service.getSortedDescByPoints();
        assertEquals(1, sorted.size());
        assertEquals(1, sorted.get(0).getRank());
        assertEquals(e2.getId(), sorted.get(0).getId());
    }
}

