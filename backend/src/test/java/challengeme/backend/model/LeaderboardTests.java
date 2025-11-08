package challengeme.backend.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardTests {

    private User user(String name) {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setUsername(name);
        return u;
    }

    @Test
    void getters_setters_work_and_rank_is_mutable() {
        Leaderboard lb = new Leaderboard();
        UUID id = UUID.randomUUID();

        lb.setId(id);
        lb.setUser(user("ana"));
        lb.setTotalPoints(123);
        lb.setRank(5);

        assertEquals(id, lb.getId());
        assertEquals(123, lb.getTotalPoints());
        assertEquals(5, lb.getRank());
        assertEquals("ana", lb.getUser().getUsername());
    }

    @Test
    void equals_and_hashCode_based_on_id() {
        User u = user("ana");
        UUID id = UUID.randomUUID();

        Leaderboard a = new Leaderboard(id, u, 10);
        Leaderboard b = new Leaderboard(id, u, 999);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
