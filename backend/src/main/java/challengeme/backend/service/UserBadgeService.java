package challengeme.backend.service;

import challengeme.backend.dto.request.update.UserBadgeUpdateRequest;
import challengeme.backend.exception.UserBadgeNotFoundException;
import challengeme.backend.model.Badge;
import challengeme.backend.model.User;
import challengeme.backend.model.UserBadge;
import challengeme.backend.repository.UserBadgeRepository;
import challengeme.backend.repository.UserRepository;
import challengeme.backend.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing the association between Users and Badges.
 * It handles the logic for awarding achievements, retrieving a user's trophy collection,
 * and maintaining the history of when badges were earned.
 */
@Service
@RequiredArgsConstructor
public class UserBadgeService {

    private final UserBadgeRepository repository;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;

    /**
     * Retrieves all user-badge records in the system.
     * @return a list of all UserBadge entries.
     */
    public List<UserBadge> findAll() {
        return repository.findAll();
    }

    /**
     * Finds a specific achievement record by its unique identifier.
     * @param id the UUID of the user-badge association.
     * @return the found UserBadge entity.
     * @throws UserBadgeNotFoundException if the record does not exist.
     */
    public UserBadge findUserBadge(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserBadgeNotFoundException("UserBadge not found with id: " + id));
    }

    /**
     * Formally awards a badge to a user.
     * This method resolves the User and Badge entities and creates a persistent link
     * with the current timestamp as the award date.
     * @param userId the UUID of the recipient user.
     * @param badgeId the UUID of the badge to be awarded.
     * @return the newly created UserBadge record.
     */
    public UserBadge createUserBadge(UUID userId, UUID badgeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found with id: " + badgeId));

        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setDateAwarded(LocalDateTime.now());

        return repository.save(userBadge);
    }

    /**
     * Retrieves the complete collection of badges earned by a specific user.
     * Directly fuels the "Trophy Case" section of the user profile.
     * @param username the username of the user.
     * @return a list of UserBadge records for the specified user.
     */
    public List<UserBadge> getBadgesByUsername(String username) {
        return repository.findAllByUser_Username(username);
    }

    /**
     * Updates an achievement record, typically to adjust the award date for administrative purposes.
     * @param id the UUID of the record to update.
     * @param request the DTO containing the updated fields.
     * @return the updated UserBadge entity.
     */
    public UserBadge updateUserBadge(UUID id, UserBadgeUpdateRequest request) {
        UserBadge existing = findUserBadge(id);
        if (request.getDateAwarded() != null) {
            existing.setDateAwarded(request.getDateAwarded());
        }
        return repository.save(existing);
    }

    /**
     * Revokes a badge from a user by deleting the association record.
     * @param id the UUID of the UserBadge record to remove.
     */
    public void deleteUserBadge(UUID id) {
        UserBadge existing = findUserBadge(id);
        repository.delete(existing);
    }
}
