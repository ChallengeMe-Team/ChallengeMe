package challengeme.backend.repository.inMemory;

import challengeme.backend.model.Leaderboard;
import challengeme.backend.repository.LeaderboardRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryLeaderboardRepository implements LeaderboardRepository {


    private final List<Leaderboard> storage = new CopyOnWriteArrayList<>();

    @Override
    public Leaderboard save(Leaderboard entry) {
        if (entry.getId() == null) {
            entry.setId(UUID.randomUUID());
            storage.add(entry);
            return entry;
        }

        for (int i = 0; i < storage.size(); i++) {
            if (Objects.equals(storage.get(i).getId(), entry.getId())) {
                storage.set(i, entry);
                return entry;
            }
        }

        storage.add(entry);
        return entry;
    }

    @Override
    public Optional<Leaderboard> findById(UUID id) {
        return storage.stream().filter(e -> Objects.equals(e.getId(), id)).findFirst();
    }

    @Override
    public List<Leaderboard> findAll() {
        return new ArrayList<>(storage);
    }

    @Override
    public void deleteById(UUID id) {
        storage.removeIf(e -> Objects.equals(e.getId(), id));
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.stream().anyMatch(e -> Objects.equals(e.getId(), id));
    }
}
