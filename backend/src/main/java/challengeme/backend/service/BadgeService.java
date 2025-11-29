package challengeme.backend.service;

import challengeme.backend.mapper.BadgeMapper;
import challengeme.backend.model.Badge;
import challengeme.backend.repository.BadgeRepository;
import challengeme.backend.dto.request.update.BadgeUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import challengeme.backend.exception.BadgeNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final BadgeMapper mapper;

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Badge getBadgeById(UUID id) {
        return badgeRepository.findById(id)
                .orElseThrow(() -> new BadgeNotFoundException("Badge with id " + id + " not found"));
    }

    public Badge createBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    public Badge updateBadge(UUID id, BadgeUpdateRequest request) {
        Badge entity = getBadgeById(id); // aruncă BadgeNotFoundException dacă nu există
        mapper.updateEntity(request, entity);
        return badgeRepository.save(entity);
    }

    public void deleteBadge(UUID id) {
        Badge entity = getBadgeById(id); // aruncă BadgeNotFoundException dacă nu există
        badgeRepository.delete(entity);
    }

}
