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

/**
 * Service class for managing the Badge catalog and user-earned achievements.
 * It provides the business logic for creating, retrieving, updating, and deleting
 * badge definitions, as well as listing badges earned by specific users.
 */
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final BadgeMapper mapper;

    /**
     * Retrieves all available badge definitions from the global catalog.
     * @return a list of all Badge entities.
     */
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    /**
     * Finds a specific badge by its unique identifier.
     * @param id the UUID of the badge.
     * @return the Badge entity if found.
     * @throws BadgeNotFoundException if the badge record does not exist.
     */
    public Badge getBadgeById(UUID id) {
        return badgeRepository.findById(id)
                .orElseThrow(() -> new BadgeNotFoundException("Badge with id " + id + " not found"));
    }

    /**
     * Registers a new badge definition in the system.
     * @param badge the Badge entity to be persisted.
     * @return the saved Badge record.
     */
    public Badge createBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    /**
     * Updates an existing badge definition based on a partial update request.
     * @param id the UUID of the badge to modify.
     * @param request the DTO containing the fields to be updated.
     * @return the updated and saved Badge entity.
     * @throws BadgeNotFoundException if the target badge is not found.
     */
    public Badge updateBadge(UUID id, BadgeUpdateRequest request) {
        Badge entity = getBadgeById(id); // Throws exception if not present
        mapper.updateEntity(request, entity);
        return badgeRepository.save(entity);
    }

    /**
     * Specialized query to retrieve all badges currently owned by a user.
     * Uses the optimized native join query from the repository.
     * @param username the identifier of the user.
     * @return a list of Badge entities earned by the specified user.
     */
    public List<Badge> getUserBadges(String username) {
        return badgeRepository.findBadgesByUsername(username);
    }

    /**
     * Permanently removes a badge definition from the system.
     * @param id the UUID of the badge to delete.
     * @throws BadgeNotFoundException if the badge cannot be located.
     */
    public void deleteBadge(UUID id) {
        Badge entity = getBadgeById(id); // Ensures entity exists before deletion
        badgeRepository.delete(entity);
    }

}
