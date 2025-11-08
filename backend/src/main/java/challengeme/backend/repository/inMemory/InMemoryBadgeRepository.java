package challengeme.backend.repository.inMemory;

import challengeme.backend.exception.BadgeNotFoundException;
import challengeme.backend.model.Badge;
import challengeme.backend.repository.BadgeRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class InMemoryBadgeRepository implements BadgeRepository {

    private final List<Badge> badges = new ArrayList<>();

    @Override
    public List<Badge> findAll() {
        return new ArrayList<>(badges);
    }

    @Override
    public Badge findById(UUID id) {
        return badges.stream().filter(badge -> badge.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BadgeNotFoundException("Badge with id " + id + " not found"));
    }

    @Override
    public Badge save(Badge badge) {
        badges.add(badge);
        return badge;
    }

    @Override
    public void delete(UUID id) {
        boolean removed = badges.removeIf(b -> b.getId().equals(id));
        if (!removed) {
            throw new BadgeNotFoundException("Badge with id " + id + " not found");
        }
    }

    @Override
    public void update(Badge badge) {
        badges.stream()
                .filter(b -> b.getId().equals(badge.getId()))
                .findFirst()
                .map(b -> {
                    b.setName(badge.getName());
                    b.setDescription(badge.getDescription());
                    b.setCriteria(badge.getCriteria());
                    return b;
                })
                .orElseThrow(() -> new BadgeNotFoundException("Badge with id " + badge.getId() + " not found"));
    }
}
