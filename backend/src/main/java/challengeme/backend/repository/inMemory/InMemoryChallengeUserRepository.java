package challengeme.backend.repository.inMemory;

import challengeme.backend.model.ChallengeUser;
import challengeme.backend.repository.ChallengeUserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementarea in-memory a ChallengeUserRepository.
 * Folosește un Map pentru a stoca datele.
 */
@Repository // Marcăm implementarea ca fiind Bean-ul Spring
public class InMemoryChallengeUserRepository implements ChallengeUserRepository {

    // Baza de date "in-memory"
    private final Map<UUID, ChallengeUser> database = new ConcurrentHashMap<>();

    @Override
    public ChallengeUser save(ChallengeUser challengeUser) {
        if (challengeUser.getId() == null) {
            challengeUser.setId(UUID.randomUUID());
        }
        database.put(challengeUser.getId(), challengeUser);
        return challengeUser;
    }

    @Override
    public Optional<ChallengeUser> findById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<ChallengeUser> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public List<ChallengeUser> findByUserId(UUID userId) {
        return database.values().stream()
                .filter(cu -> cu.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        database.remove(id);
    }
}
