package challengeme.backend.service;

import challengeme.backend.dto.BadgeDTO;
import challengeme.backend.dto.ChallengeHistoryDTO;
import challengeme.backend.dto.FriendDTO;
import challengeme.backend.dto.UserProfileDTO;
import challengeme.backend.dto.request.create.NotificationCreateRequest;
import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.mapper.BadgeMapper;
import challengeme.backend.mapper.UserMapper;
import challengeme.backend.model.*;
import challengeme.backend.repository.BadgeRepository;
import challengeme.backend.repository.ChallengeUserRepository;
import challengeme.backend.repository.UserBadgeRepository;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import challengeme.backend.dto.request.update.ChangePasswordRequest;

/**
 * Main service responsible for user management, social networking, and profile aggregation.
 * It orchestrates complex business rules like badge awarding, activity history sorting,
 * and bidirectional friendship consistency.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ChallengeUserRepository challengeUserRepository;
    private final BadgeRepository badgeRepository;
    private final UserMapper mapper;
    private final BadgeMapper badgeMapper;
    private final PasswordEncoder passwordEncoder;
    private final ChallengeService challengeService;
    private final UserBadgeRepository userBadgeRepository;
    private final NotificationService notificationService;

    // -----------------------------------------------------------
    // BASIC CRUD & AUTHENTICATION
    // -----------------------------------------------------------

    /** Retrieves a list of all users registered in the system. */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Finds a user by their unique UUID.
     * @param id The unique identifier of the user.
     * @return The User entity.
     * @throws UserNotFoundException if the user does not exist.
     */
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    }

    /** Saves a new user record to the database. */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /** Deletes a user account by their UUID. */
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    /**
     * Updates user profile data (username, email, avatar).
     * If the username is updated, it triggers a synchronization across challenges to ensure data integrity.
     * @param id The ID of the user to update.
     * @param request The DTO containing the new profile details.
     * @return The updated User entity.
     */
    @Transactional
    public User updateUser(UUID id, UserUpdateRequest request) {
        User user = getUserById(id);
        String oldUsername = user.getUsername();
        boolean usernameChanged = false;

        if (request.username() != null && !request.username().isBlank()) {
            if (!request.username().equals(user.getUsername())) {
                if (userRepository.existsByUsername(request.username())) {
                    throw new RuntimeException("Username already taken");
                }
                user.setUsername(request.username());
                usernameChanged = true;
            }
        }

        if (request.email() != null && !request.email().isBlank()) {
            if (!request.email().equals(user.getEmail())) {
                if (userRepository.existsByEmail(request.email())) {
                    throw new RuntimeException("Email already taken");
                }
                user.setEmail(request.email());
            }
        }

        if (request.avatar() != null) {
            user.setAvatar(request.avatar());
        }

        User updatedUser = userRepository.save(user);

        if (usernameChanged) {
            challengeService.synchronizeUsername(oldUsername, updatedUser.getUsername());
        }

        return updatedUser;
    }

    /** Checks if a specific username is already taken. */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /** Checks if a specific email is already registered. */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Changes the user's password after validating the current password matches.
     * @param userId The ID of the user.
     * @param request The DTO containing current and new passwords.
     */
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = getUserById(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect current password");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    /** Finds a user by their username string. */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Retrieves the friend list for a user.
     * @param currentUserId The UUID of the user.
     * @return A list of simplified FriendDTO objects.
     */
    public List<FriendDTO> getUserFriends(UUID currentUserId) {
        User currentUser = getUserById(currentUserId);
        List<UUID> friendIds = currentUser.getFriendIds();
        if (friendIds == null || friendIds.isEmpty()) {
            return new ArrayList<>();
        }
        return userRepository.findAllById(friendIds).stream()
                .map(mapper::toFriendDTO)
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------
    // SOCIAL FEATURES (Friendships)
    // -----------------------------------------------------------

    /**
     * Establishes a bidirectional friendship between two users and sends a system notification.
     * @param currentUserId The ID of the user initiating the friendship.
     * @param friendUsername The username of the target friend.
     */
    @Transactional
    public void addFriend(UUID currentUserId, String friendUsername) {
        User current = getUserById(currentUserId);
        if (current.getUsername().equalsIgnoreCase(friendUsername)) {
            throw new RuntimeException("You cannot add yourself");
        }
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (current.getFriendIds().contains(friend.getId())) {
            return;
        }
        current.getFriendIds().add(friend.getId());
        if (!friend.getFriendIds().contains(current.getId())) {
            friend.getFriendIds().add(current.getId());
        }
        userRepository.save(current);
        userRepository.save(friend);

        notificationService.createNotification(new NotificationCreateRequest(
                friend.getId(),
                current.getUsername() + " added you as a friend!",
                NotificationType.SYSTEM
        ));
    }

   /**
     * Maintenance method to ensure all friendship links are bidirectional across the DB.
     * @return A map containing stats about fixed connections.
     */
    @Transactional
    public Map<String, Integer> syncAllFriendships() {
        List<User> allUsers = userRepository.findAll();
        int fixedCount = 0;
        for (User userA : allUsers) {
            if (userA.getFriendIds() == null) continue;
            for (UUID friendIdB : new ArrayList<>(userA.getFriendIds())) {
                userRepository.findById(friendIdB).ifPresent(userB -> {
                    if (userB.getFriendIds() == null) userB.setFriendIds(new ArrayList<>());
                    if (!userB.getFriendIds().contains(userA.getId())) {
                        userB.getFriendIds().add(userA.getId());
                        userRepository.save(userB);
                    }
                });
            }
        }
        return Map.of("fixedConnections", fixedCount);
    }

    /**
     * Removes the friendship link between two users in both directions.
     * @param currentUserId Initiator ID.
     * @param targetId Target ID to be removed.
     */
    @Transactional
    public void removeFriend(UUID currentUserId, UUID targetId) {
        User current = getUserById(currentUserId);
        User target = getUserById(targetId);
        current.getFriendIds().removeIf(id -> id.equals(targetId));
        target.getFriendIds().removeIf(id -> id.equals(currentUserId));
        userRepository.save(current);
        userRepository.save(target);
    }

    // -----------------------------------------------------------
    // PROFILE & ACHIEVEMENTS LOGIC
    // -----------------------------------------------------------

    /**
     * Helper to retrieve the profile of the currently authenticated user.
     * @return The aggregated UserProfileDTO.
     */
    public UserProfileDTO getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        return getUserProfileById(user.getId());
    }

    /**
     * Logic to automatically award badges based on user performance and categories.
     * Triggers notifications and persists UserBadge records upon meeting criteria.
     * @param user The user to evaluate.
     * @param completedChallenges The list of completed quests.
     * @return A list of BadgeDTOs currently owned by the user.
     */
    public List<BadgeDTO> generateBadges(User user, List<ChallengeUser> completedChallenges) {
        List<BadgeDTO> ownedDTOs = new ArrayList<>();
        List<Badge> allPossibleBadges = badgeRepository.findAll();

        // Rules engine for badges
        if (!completedChallenges.isEmpty()) {
            findAndAddBadge(user, ownedDTOs, allPossibleBadges, "First Step");
        }

        long fitnessCount = completedChallenges.stream()
                .filter(cu -> "Fitness".equalsIgnoreCase(cu.getChallenge().getCategory())).count();
        if (fitnessCount >= 3) {
            findAndAddBadge(user, ownedDTOs, allPossibleBadges, "Marathoner");
        }

        long mindfulnessCount = completedChallenges.stream()
                .filter(cu -> "Mindfulness".equalsIgnoreCase(cu.getChallenge().getCategory())).count();
        if (mindfulnessCount >= 5) {
            findAndAddBadge(user, ownedDTOs, allPossibleBadges, "Zen Master");
        }

        long eduCount = completedChallenges.stream()
                .filter(cu -> "Education".equalsIgnoreCase(cu.getChallenge().getCategory())).count();
        if (eduCount >= 3) {
            findAndAddBadge(user, ownedDTOs, allPossibleBadges, "Polyglot");
        }

        long codingCount = completedChallenges.stream()
                .filter(cu -> "Coding".equalsIgnoreCase(cu.getChallenge().getCategory())).count();
        if (codingCount >= 3) {
            findAndAddBadge(user, ownedDTOs, allPossibleBadges, "Code Ninja");
        }

        boolean hasChef = completedChallenges.stream()
                .anyMatch(cu -> "Weekend Chef".equalsIgnoreCase(cu.getChallenge().getTitle()));
        if (hasChef) {
            findAndAddBadge(user, ownedDTOs, allPossibleBadges, "Weekend Chef");
        }

        return ownedDTOs;
    }

    /**
     * Internal helper to verify badge ownership, award it if missing, and send a notification.
     */
    private void findAndAddBadge(User user, List<BadgeDTO> targetList, List<Badge> sourceList, String badgeName) {
        sourceList.stream()
                .filter(b -> b.getName().equalsIgnoreCase(badgeName))
                .findFirst()
                .ifPresent(badge -> {
                    if (!userBadgeRepository.existsByUserIdAndBadgeId(user.getId(), badge.getId())) {
                        UserBadge ub = new UserBadge();
                        ub.setUser(user);
                        ub.setBadge(badge);
                        ub.setDateAwarded(LocalDateTime.now());
                        userBadgeRepository.save(ub);

                        String message = "Congratulations! You've unlocked the " + badge.getName() + " badge!";
                        NotificationCreateRequest notifRequest = new NotificationCreateRequest(
                                user.getId(),
                                message,
                                NotificationType.BADGE
                        );
                        notificationService.createNotification(notifRequest);
                    }
                    targetList.add(badgeMapper.toDTO(badge));
                });
    }

    /**
     * Calculates the daily completion streak.
     * A streak increases if challenges are completed on consecutive days.
     * @param completedChallenges List of successfully finished quests.
     * @return The number of consecutive active days.
     */
    private int calculateStreak(List<ChallengeUser> completedChallenges) {
        if (completedChallenges.isEmpty()) return 0;

        List<LocalDate> dates = completedChallenges.stream()
                .map(cu -> cu.getDateCompleted().toLocalDate())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
        if (dates.isEmpty()) return 0;
        int streak = 0;
        LocalDate current = LocalDate.now();
        if (!dates.contains(current) && !dates.contains(current.minusDays(1))) return 0;
        LocalDate checkDate = dates.getFirst();
        for (LocalDate date : dates) {
            if (date.equals(checkDate)) { streak++; checkDate = checkDate.minusDays(1); } else break;
        }
        return streak;
    }

    // -----------------------------------------------------------
    // GAMIFICATION & PROFILE LOGIC
    // -----------------------------------------------------------

    /**
     * Orchestrates the construction of the full User Profile.
     * Aggregates stats, levels, streaks, badges, and activity history.
     * @param id User UUID.
     * @return The complete UserProfileDTO.
     */
    public UserProfileDTO getUserProfileById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<ChallengeUser> userChallenges = challengeUserRepository.findByUserId(id);
        List<ChallengeUser> completed = userChallenges.stream()
                .filter(cu -> cu.getStatus() == ChallengeUserStatus.COMPLETED)
                .toList();

        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPoints() != null ? user.getPoints() : 0,
                ((user.getPoints() != null ? user.getPoints() : 0) / 100) + 1,
                user.getAvatar(),
                user.getTotalCompletedChallenges() != null ? user.getTotalCompletedChallenges() : 0,
                calculateStreak(completed),
                generateBadges(user, completed),
                getSortedActivity(userChallenges, user.getUsername()),
                getSkillsMap(completed)
        );
    }

    /**
     * Merges challenge history and badge unlocks into a single chronological feed.
     * @param userChallenges List of user-quest participations.
     * @param username Target username.
     * @return A sorted list of the last 10 activities.
     */
    private List<ChallengeHistoryDTO> getSortedActivity(List<ChallengeUser> userChallenges, String username) {
        List<UserBadge> userBadges = userBadgeRepository.findAllByUser_Username(username);
        List<ChallengeHistoryDTO> combinedActivity = new ArrayList<>();

        userChallenges.forEach(cu -> {
            String activityDate = "N/A";

            if (cu.getDateCompleted() != null) {
                activityDate = cu.getDateCompleted().toString(); // ex: 2024-01-14T11:45:30
            }
            else if (cu.getStartDate() != null) {
                activityDate = cu.getStartDate().toString();
            }

            combinedActivity.add(new ChallengeHistoryDTO(
                    cu.getChallenge().getTitle(),
                    cu.getStatus().toString(),
                    activityDate,
                    cu.getTimesCompleted() != null ? cu.getTimesCompleted() : 0
            ));
        });

        userBadges.forEach(ub -> combinedActivity.add(new ChallengeHistoryDTO(
                ub.getBadge().getName(),
                "BADGE_UNLOCKED",
                ub.getDateAwarded().toString(),
                1
        )));

        return combinedActivity.stream()
                .sorted(Comparator.comparing(ChallengeHistoryDTO::date).reversed())
                .limit(10)
                .toList();
    }

    /**
     * Calculates a map of skills based on points earned per category.
     * @param completed List of completed challenges.
     * @return A map where Key = Category and Value = Sum of Points.
     */
    private Map<String, Integer> getSkillsMap(List<ChallengeUser> completed) {
        return completed.stream()
                .collect(Collectors.groupingBy(
                        cu -> cu.getChallenge().getCategory(),
                        Collectors.summingInt(cu -> cu.getChallenge().getPoints())
                ));
    }
}