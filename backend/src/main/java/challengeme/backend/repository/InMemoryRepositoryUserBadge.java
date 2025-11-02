package challengeme.backend.repository;

import challengeme.backend.exception.EntityNotFoundException;
import challengeme.backend.model.UserBadge;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class InMemoryRepositoryUserBadge implements RepositoryUserBadge {

    private final List<UserBadge> userBadges = new ArrayList<>();

    @Override
    public List<UserBadge> getAll() {
        return new ArrayList<>(userBadges);
    }

    @Override
    public UserBadge getUserBadge(UUID id) {
        return userBadges.stream()
                .filter(ub -> ub.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("UserBadge with id " + id + " not found"));
    }

    @Override
    public UserBadge create(UserBadge userBadge) {
        userBadges.add(userBadge);
        return userBadge;
    }

    @Override
    public void delete(UUID id) {
        boolean removed = userBadges.removeIf(ub -> ub.getId().equals(id));
        if (!removed) {
            throw new EntityNotFoundException("UserBadge with id " + id + " not found");
        }
    }

    @Override
    public void update(UserBadge userBadge) {
        userBadges.stream()
                .filter(ub -> ub.getId().equals(userBadge.getId()))
                .findFirst()
                .map(existing -> {
                    existing.setUser(userBadge.getUser());
                    existing.setBadge(userBadge.getBadge());
                    existing.setDateAwarded(userBadge.getDateAwarded());
                    return existing;
                })
                .orElseThrow(() -> new EntityNotFoundException("UserBadge with id " + userBadge.getId() + " not found"));
    }
}
