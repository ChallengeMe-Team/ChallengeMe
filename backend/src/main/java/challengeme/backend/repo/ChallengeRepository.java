package challengeme.backend.repo;

import challengeme.backend.domain.Challenge;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ChallengeRepository implements ChallengeRepoInterface{

    private final Map<UUID, Challenge> storage = new ConcurrentHashMap<>();

    @Override
    public List<Challenge> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Challenge> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Challenge save(Challenge challenge) {
        if(challenge.getId() == null){
            challenge.setId(UUID.randomUUID());
        }
        storage.put(challenge.getId(), challenge);
        return challenge;
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }
}
