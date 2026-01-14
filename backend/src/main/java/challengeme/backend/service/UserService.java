package challengeme.backend.service;

import challengeme.backend.dto.BadgeDTO;
import challengeme.backend.dto.ChallengeHistoryDTO;
import challengeme.backend.dto.FriendDTO;
import challengeme.backend.dto.UserProfileDTO;
import challengeme.backend.dto.request.create.NotificationCreateRequest; // Added Import
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

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = getUserById(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect current password");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

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

    @Transactional
    public void addFriend(UUID currentUserId, String friendUsername) {
        User current = getUserById(currentUserId);
        if (current.getUsername().equalsIgnoreCase(friendUsername)) {
            throw new RuntimeException("You cannot add yourself");
        }
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (current.getFriendIds().contains(friend.getId())) {
            return; // Nu facem nimic dacă sunt deja prieteni, evităm duplicarea notificărilor
        }
        current.getFriendIds().add(friend.getId());
        if (!friend.getFriendIds().contains(current.getId())) {
            friend.getFriendIds().add(current.getId());
        }
        userRepository.save(current);
        userRepository.save(friend);

        notificationService.createNotification(new NotificationCreateRequest(
                friend.getId(), // Cel care primește notificarea
                current.getUsername() + " added you as a friend!",
                NotificationType.SYSTEM
        ));
    }

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
    public UserProfileDTO getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        return getUserProfileById(user.getId());
    }

    public List<BadgeDTO> generateBadges(User user, List<ChallengeUser> completedChallenges) {
        List<BadgeDTO> ownedDTOs = new ArrayList<>();
        List<Badge> allPossibleBadges = badgeRepository.findAll();

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

    private void findAndAddBadge(User user, List<BadgeDTO> targetList, List<Badge> sourceList, String badgeName) {
        sourceList.stream()
                .filter(b -> b.getName().equalsIgnoreCase(badgeName))
                .findFirst()
                .ifPresent(badge -> {
                    // Verificăm că notificarea se trimite doar o dată per badge
                    // Ne bazăm pe existența in Baza de Date. Dacă nu este,o creăm și o trimitem.
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

    private int calculateStreak(List<ChallengeUser> completedChallenges) {
        if (completedChallenges.isEmpty()) return 0;

        List<LocalDate> dates = completedChallenges.stream()
                // EXTRAGE DOAR DATA (LocalDate) din LocalDateTime pentru calculul streak-ului
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

    public UserProfileDTO getUserProfileById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<ChallengeUser> userChallenges = challengeUserRepository.findByUserId(id);
        List<ChallengeUser> completed = userChallenges.stream()
                .filter(cu -> cu.getStatus() == ChallengeUserStatus.COMPLETED)
                .toList();

        // Folosim metodele helper de mai jos
        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPoints() != null ? user.getPoints() : 0,
                ((user.getPoints() != null ? user.getPoints() : 0) / 100) + 1,
                user.getAvatar(),
                user.getTotalCompletedChallenges() != null ? user.getTotalCompletedChallenges() : 0,
                calculateStreak(completed),
                generateBadges(user, completed), // Aceasta metodă este deja definită
                getSortedActivity(userChallenges, user.getUsername()), // Metoda nouă mai jos
                getSkillsMap(completed) // Metoda nouă mai jos
        );
    }

    // METODE HELPER PENTRU REUTILIZARE LOGICĂ PROFIL
    private List<ChallengeHistoryDTO> getSortedActivity(List<ChallengeUser> userChallenges, String username) {
        List<UserBadge> userBadges = userBadgeRepository.findAllByUser_Username(username);
        List<ChallengeHistoryDTO> combinedActivity = new ArrayList<>();

        userChallenges.forEach(cu -> {
            String activityDate = "N/A";

            // Dacă e finalizată, avem LocalDateTime (dată + oră exactă)
            if (cu.getDateCompleted() != null) {
                activityDate = cu.getDateCompleted().toString(); // ex: 2024-01-14T11:45:30
            }
            // Dacă e doar acceptată, avem LocalDate.
            // Îi adăugăm simbolic ora 00:00 pentru a nu strica sortarea
            else if (cu.getStartDate() != null) {
                activityDate = cu.getStartDate().atStartOfDay().toString();
            }

            combinedActivity.add(new ChallengeHistoryDTO(
                    cu.getChallenge().getTitle(),
                    cu.getStatus().toString(),
                    activityDate,
                    cu.getTimes_completed() != null ? cu.getTimes_completed() : 0
            ));
        });

        userBadges.forEach(ub -> combinedActivity.add(new ChallengeHistoryDTO(
                ub.getBadge().getName(),
                "BADGE_UNLOCKED",
                ub.getDateAwarded().toString(), // Va conține ora exactă a salvării
                1
        )));

        return combinedActivity.stream()
                // Sortează cronologic după String-ul ISO (care funcționează bine alfabetic)
                .sorted(Comparator.comparing(ChallengeHistoryDTO::date).reversed())
                .limit(10)
                .toList();
    }

    private Map<String, Integer> getSkillsMap(List<ChallengeUser> completed) {
        return completed.stream()
                .collect(Collectors.groupingBy(
                        cu -> cu.getChallenge().getCategory(),
                        Collectors.summingInt(cu -> cu.getChallenge().getPoints())
                ));
    }
}