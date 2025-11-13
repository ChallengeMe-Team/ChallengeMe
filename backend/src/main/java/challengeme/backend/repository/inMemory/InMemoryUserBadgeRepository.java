package challengeme.backend.repository.inMemory;

import challengeme.backend.exception.UserBadgeNotFoundException;
import challengeme.backend.model.UserBadge;
import challengeme.backend.repository.RepositoryUserBadge;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class InMemoryUserBadgeRepository implements RepositoryUserBadge {

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
                .orElseThrow(() -> new UserBadgeNotFoundException("UserBadge with id " + id + " not found"));
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
            throw new UserBadgeNotFoundException("UserBadge with id " + id + " not found");
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
                .orElseThrow(() -> new UserBadgeNotFoundException("UserBadge with id " + userBadge.getId() + " not found"));
    }
}
