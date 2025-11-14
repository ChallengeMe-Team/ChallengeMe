package challengeme.backend.repository.inMemory;

import challengeme.backend.exception.LeaderboardNotFoundException;
import challengeme.backend.model.Leaderboard;
import challengeme.backend.repository.LeaderboardRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
public class InMemoryLeaderboardRepository implements LeaderboardRepository {

    private final List<Leaderboard> storage = new ArrayList<>();

    @Override
    public List<Leaderboard> findAll() {
        return new ArrayList<>(storage);
    }

    @Override
    public Leaderboard findById(UUID id) {
        return storage.stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .findFirst()
                .orElseThrow(() -> new LeaderboardNotFoundException(id));
    }

    @Override
    public Leaderboard save(Leaderboard entry) {
        if (entry.getId() == null) {
            entry.setId(UUID.randomUUID());
        }
        storage.add(entry);
        return entry;
    }

    @Override
    public void delete(UUID id) {
        boolean removed = storage.removeIf(e -> Objects.equals(e.getId(), id));
        if (!removed) throw new LeaderboardNotFoundException(id);
    }

    @Override
    public void update(Leaderboard entry) {
        if (entry.getId() == null) {
            throw new IllegalArgumentException("Update requires a non-null id.");
        }
        for (int i = 0; i < storage.size(); i++) {
            if (Objects.equals(storage.get(i).getId(), entry.getId())) {
                storage.set(i, entry);
                return;
            }
        }
        throw new LeaderboardNotFoundException(entry.getId());
    }

    private boolean exists(UUID id) {
        for (Leaderboard e : storage) {
            if (Objects.equals(e.getId(), id)) return true;
        }
        return false;
    }
}
