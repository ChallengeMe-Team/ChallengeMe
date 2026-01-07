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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserBadgeService {

    private final UserBadgeRepository repository;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;


    public List<UserBadge> findAll() {
        return repository.findAll();
    }

    public UserBadge findUserBadge(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserBadgeNotFoundException("UserBadge not found with id: " + id));
    }

    public UserBadge createUserBadge(UUID userId, UUID badgeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found with id: " + badgeId));

        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setDateAwarded(LocalDate.now());

        return repository.save(userBadge);
    }

    public List<UserBadge> getBadgesByUsername(String username) {
        return repository.findAllByUser_Username(username);
    }

    public UserBadge updateUserBadge(UUID id, UserBadgeUpdateRequest request) {
        UserBadge existing = findUserBadge(id);
        if (request.getDateAwarded() != null) {
            existing.setDateAwarded(request.getDateAwarded());
        }
        return repository.save(existing);
    }

    public void deleteUserBadge(UUID id) {
        UserBadge existing = findUserBadge(id);
        repository.delete(existing);
    }
}
