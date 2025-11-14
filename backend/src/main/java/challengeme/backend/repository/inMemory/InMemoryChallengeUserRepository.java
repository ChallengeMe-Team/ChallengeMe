package challengeme.backend.repository.inMemory;

import challengeme.backend.model.ChallengeUser;
import challengeme.backend.repository.ChallengeUserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Repository
public class InMemoryChallengeUserRepository implements ChallengeUserRepository {

    private final List<ChallengeUser> database = new CopyOnWriteArrayList<>();

    @Override
    public ChallengeUser save(ChallengeUser challengeUser) {
        if (challengeUser.getId() == null) {
            challengeUser.setId(UUID.randomUUID());
            database.add(challengeUser);
        } else {
            Optional<ChallengeUser> existing = findById(challengeUser.getId());
            existing.ifPresent(database::remove);
            database.add(challengeUser);
        }
        return challengeUser;
    }

    @Override
    public Optional<ChallengeUser> findById(UUID id) {
        return database.stream()
                .filter(cu -> cu.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<ChallengeUser> findAll() {
        return new ArrayList<>(database);
    }

    @Override
    public List<ChallengeUser> findByUserId(UUID userId) {
        return database.stream()
                .filter(cu -> cu.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        database.removeIf(cu -> cu.getId().equals(id));
    }
}
