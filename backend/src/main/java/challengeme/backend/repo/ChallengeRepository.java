package challengeme.backend.repo;

import challengeme.backend.domain.Challenge;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ChallengeRepository {
    private final Map<UUID, Challenge> storage = new HashMap<>();

    public List<Challenge> findAll() {
        return new ArrayList<>(storage.values());
    }
    public Challenge findById(UUID id) {
        return storage.get(id);
    }

    public Challenge save(Challenge challenge) {
        storage.put(challenge.getId(), challenge);
        return challenge;
    }

    public void deleteById(UUID id) {
        storage.remove(id);
    }

    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }
}
