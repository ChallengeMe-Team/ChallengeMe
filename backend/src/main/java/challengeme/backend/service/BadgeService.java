package challengeme.backend.service;

import challengeme.backend.model.Badge;
import challengeme.backend.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;

    @Autowired
    public BadgeService(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Badge getBadgeById(UUID id) {
        return badgeRepository.findById(id);
    }

    public Badge createBadge(Badge badge) {
        if(badge.getId() == null) {
            badge.setId(UUID.randomUUID());
        }
        return badgeRepository.save(badge);
    }

    public void deleteBadge(UUID id) {
        badgeRepository.delete(id);
    }

    public Badge updateBadge(UUID id, Badge badge) {
        Badge existing = badgeRepository.findById(id);
        badge.setId(existing.getId());
        badgeRepository.update(badge);
        return badge;
    }

}
