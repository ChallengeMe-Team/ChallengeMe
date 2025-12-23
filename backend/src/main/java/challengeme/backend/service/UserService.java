package challengeme.backend.service;

import challengeme.backend.dto.BadgeDTO;
import challengeme.backend.dto.ChallengeHistoryDTO;
import challengeme.backend.dto.FriendDTO;
import challengeme.backend.dto.UserProfileDTO;
import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.mapper.UserMapper;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.ChallengeUserStatus;
import challengeme.backend.model.User;
import challengeme.backend.repository.ChallengeUserRepository;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import challengeme.backend.dto.request.update.ChangePasswordRequest;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ChallengeUserRepository challengeUserRepository;
    private final UserMapper mapper;

    private final PasswordEncoder passwordEncoder;

    //Am adăugat ChallengeService pentru sincronizare ---
    private final ChallengeService challengeService;

    // -----------------------------------------------------------
    // BASIC CRUD
    // -----------------------------------------------------------
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    // Update User with Uniqueness Check
    @Transactional // Recomandat Transactional aici
    public User updateUser(UUID id, UserUpdateRequest request) {
        User user = getUserById(id);

        // Salvăm username-ul vechi ÎNAINTE de update
        String oldUsername = user.getUsername();
        boolean usernameChanged = false;

        // 1. Update Username
        // Folosim request.username() fără "get" (fiind Record)
        if (request.username() != null && !request.username().isBlank()) {
            if (!request.username().equals(user.getUsername())) {
                if (userRepository.existsByUsername(request.username())) {
                    throw new RuntimeException("Username already taken");
                }
                user.setUsername(request.username());
                usernameChanged = true; // Marcăm că s-a schimbat
            }
        }

        // 2. Update Email
        // Folosim request.email() fără "get"
        if (request.email() != null && !request.email().isBlank()) {
            // Verificăm dacă emailul e diferit și dacă e deja luat
            if (!request.email().equals(user.getEmail())) {
                if (userRepository.existsByEmail(request.email())) {
                    throw new RuntimeException("Email already taken");
                }
                user.setEmail(request.email());
            }
        }

        // 3. Update Avatar
        // Folosim request.avatar() fără "get"
        if (request.avatar() != null) {
            user.setAvatar(request.avatar());
        }

        // Salvăm userul
        User updatedUser = userRepository.save(user);

        // 4. SINCRONIZARE
        if (usernameChanged) {
            // Dacă și-a schimbat numele, actualizăm toate provocările create de el
            challengeService.synchronizeUsername(oldUsername, updatedUser.getUsername());
        }

        return updatedUser;
    } // Aici se închide metoda updateUser (aveai o acoladă în plus înainte)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = getUserById(userId);

        // Verific daca parola curenta (raw) se potriveste cu cea din baza de date (hash)
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect current password");
        }

        // Criptez noua parola
        String newHash = passwordEncoder.encode(request.newPassword());
        user.setPassword(newHash);

        userRepository.save(user);
    }

    // -----------------------------------------------------------
    // SEARCH USER BY USERNAME
    // -----------------------------------------------------------
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // -----------------------------------------------------------
    // GET FRIENDS OF USER
    // -----------------------------------------------------------
    public List<FriendDTO> getUserFriends(UUID currentUserId) {
        User currentUser = getUserById(currentUserId);

        List<UUID> friendIds = currentUser.getFriendIds();
        if (friendIds == null || friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> friends = userRepository.findAllById(friendIds);

        return friends.stream()
                .map(mapper::toFriendDTO)
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------
    // ADD FRIEND LOGIC
    // -----------------------------------------------------------
    @Transactional
    public void addFriend(UUID currentUserId, String friendUsername) {

        User current = getUserById(currentUserId);

        // Rule: cannot add yourself
        if (current.getUsername().equalsIgnoreCase(friendUsername)) {
            throw new RuntimeException("You cannot add yourself");
        }

        // Find target friend
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Rule: cannot add existing friend
        if (current.getFriendIds().contains(friend.getId())) {
            throw new RuntimeException("User is already your friend");
        }

        // Add
        current.getFriendIds().add(friend.getId());
        userRepository.save(current);
    }

    public UserProfileDTO getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        List<ChallengeUser> userChallenges = challengeUserRepository.findByUserId(user.getId());

        List<ChallengeUser> completed = userChallenges.stream()
                .filter(cu -> cu.getStatus() == ChallengeUserStatus.COMPLETED)
                .toList();

        int points = user.getPoints() != null ? user.getPoints() : 0;

        List<BadgeDTO> badges = generateBadges(user, completed.size());

        List<ChallengeHistoryDTO> recentActivity = userChallenges.stream()
                .sorted(Comparator.comparing(ChallengeUser::getStartDate).reversed())
                .limit(5)
                .map(cu -> new ChallengeHistoryDTO(
                        cu.getChallenge().getTitle(),
                        cu.getStatus().toString(),
                        cu.getDateCompleted() != null ? cu.getDateCompleted().toString() : "Recent"
                ))
                .toList();

        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                points,
                (points / 100) + 1,
                user.getAvatar(),
                completed.size(),
                calculateStreak(completed),
                badges,
                recentActivity
        );
    }

    private List<BadgeDTO> generateBadges(User user, int completedCount) {
        List<BadgeDTO> badges = new ArrayList<>();
        int points = user.getPoints() != null ? user.getPoints() : 0;

        if (completedCount >= 1) {
            badges.add(new BadgeDTO(
                    UUID.randomUUID(),
                    "First Step",
                    "Completed your first challenge",
                    "1 Challenge"
            ));
        }

        if (completedCount >= 5) {
            badges.add(new BadgeDTO(
                    UUID.randomUUID(),
                    "High Five",
                    "Completed 5 challenges",
                    "5 Challenges"
            ));
        }

        if (points >= 500) {
            badges.add(new BadgeDTO(
                    UUID.randomUUID(),
                    "Points Master",
                    "Earned 500+ XP",
                    "500 XP"
            ));
        }

        return badges;
    }

    private int calculateStreak(List<ChallengeUser> completedChallenges) {
        if (completedChallenges.isEmpty()) return 0;
        List<LocalDate> dates = completedChallenges.stream()
                .map(ChallengeUser::getDateCompleted)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
        if (dates.isEmpty()) return 0;
        int streak = 0;
        LocalDate current = LocalDate.now();
        if (!dates.contains(current) && !dates.contains(current.minusDays(1))) return 0;
        LocalDate checkDate = dates.get(0);
        for (LocalDate date : dates) {
            if (date.equals(checkDate)) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else break;
        }
        return streak;
    }
}
