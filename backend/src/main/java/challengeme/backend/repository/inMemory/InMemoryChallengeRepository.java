package challengeme.backend.repository.inMemory;

import challengeme.backend.model.Challenge;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryChallengeRepository implements challengeme.backend.repository.ChallengeRepository {

    private final List<Challenge> storage = new ArrayList<>();

    @Override
    public List<Challenge> findAll() {
        return new ArrayList<>(storage);
    }

    @Override
    public Optional<Challenge> findById(UUID id) {
        return storage.stream().filter(challenge -> challenge.getId().equals(id)).findFirst();
    }

    @Override
    public Challenge save(Challenge challenge) {
        if (challenge.getId() == null) {
            challenge.setId(UUID.randomUUID());
        } else {
            // remove existing one if updating
            deleteById(challenge.getId());
        }
        storage.add(challenge);
        return challenge;
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresent(storage::remove);
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }
}
