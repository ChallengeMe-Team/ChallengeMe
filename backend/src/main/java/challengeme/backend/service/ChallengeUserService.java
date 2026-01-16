package challengeme.backend.service;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.dto.request.create.ChallengeUserCreateRequest;
import challengeme.backend.dto.request.create.NotificationCreateRequest;
import challengeme.backend.dto.request.update.ChallengeUserUpdateRequest;
import challengeme.backend.dto.request.update.UpdateChallengeRequest;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.exception.ChallengeUserNotFoundException;
import challengeme.backend.exception.ConflictException;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.model.*;
import challengeme.backend.repository.ChallengeRepository;
import challengeme.backend.repository.ChallengeUserRepository;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import challengeme.backend.model.NotificationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The core engine of the application. Manages the lifecycle of user participation
 * in challenges, including social assignments, point rewards, and status transitions.
 */
@Service
@RequiredArgsConstructor
public class ChallengeUserService {

    private final ChallengeUserRepository repository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final NotificationService notificationService;

    /**
     * Standard creation of a challenge-user link (e.g., when a user clicks 'Start').
     */
    public ChallengeUser createChallengeUser(ChallengeUserCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));
        Challenge challenge = challengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new ChallengeNotFoundException(request.getChallengeId()));

        ChallengeUser link = new ChallengeUser();
        link.setUser(user);
        link.setChallenge(challenge);
        link.setStatus(ChallengeUserStatus.PENDING);
        link.setAssignedBy(user.getId());
        return repository.save(link);
    }

    public List<ChallengeUser> getAllChallengeUsers() { return repository.findAll(); }

    public ChallengeUser getChallengeUserById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ChallengeUserNotFoundException("ChallengeUser not found with id: " + id));
    }

    /**
     * Formally accepts a challenge, setting start dates and deadlines.
     * Prevents duplicates and handles timezone-aware timestamps.
     */
    @Transactional
    public ChallengeUserDTO acceptChallenge(UUID challengeId, String username, UpdateChallengeRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ChallengeNotFoundException(challengeId));

        if (repository.existsByUserIdAndChallengeId(user.getId(), challenge.getId())) {
            throw new ConflictException("You already have this challenge in your list or inbox.");
        }

        ChallengeUser challengeUser = new ChallengeUser();
        challengeUser.setUser(user);
        challengeUser.setChallenge(challenge);
        challengeUser.setStatus(ChallengeUserStatus.valueOf(request.getStatus()));
        if (request.getStartDate() != null) {
            challengeUser.setStartDate(LocalDateTime.now());
            LocalDateTime bucharestTime = ZonedDateTime.now(ZoneId.of("Europe/Bucharest")).toLocalDateTime();
            System.out.println("DEBUG BACKEND - Ora generată pentru acceptare: " + bucharestTime);
            challengeUser.setDateAccepted(LocalDateTime.now(ZoneId.of("UTC")));
        }
        if (request.getTargetDeadline() != null) {
            challengeUser.setDeadline(request.getTargetDeadline());
        }
        challengeUser.setAssignedBy(user.getId());
        ChallengeUser saved = repository.save(challengeUser);
        System.out.println("DEBUG BACKEND - S-a salvat în DB cu ID: " + saved.getId());
        return convertToDto(saved);
    }

    private ChallengeUserDTO convertToDto(ChallengeUser entity) {
        ChallengeUserDTO dto = new ChallengeUserDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
        }
        if (entity.getChallenge() != null) {
            Challenge c = entity.getChallenge();
            dto.setChallengeId(c.getId());
            dto.setChallengeTitle(c.getTitle());
            dto.setDescription(c.getDescription());
            dto.setPoints(c.getPoints());
            dto.setCategory(c.getCategory());
            dto.setDifficulty(c.getDifficulty().toString());
            dto.setChallengeCreatedBy(c.getCreatedBy());
        }
        dto.setStatus(entity.getStatus());
        dto.setStartDate(entity.getStartDate());
        dto.setDateAccepted(entity.getDateAccepted());
        dto.setDateCompleted(entity.getDateCompleted());
        dto.setDeadline(entity.getDeadline());

        dto.setTimesCompleted(entity.getTimesCompleted());
        return dto;
    }

    public List<ChallengeUser> getChallengeUsersByUserId(UUID userId) { return repository.findByUserId(userId); }

    /**
     * Logic for social interaction: Assigning a challenge to a friend.
     * Includes validations for self-assignment and anti-spam measures.
     */
    @Transactional
    public ChallengeUser assignChallenge(ChallengeUserCreateRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));

        if (currentUser.getId().equals(request.getUserId())) {
            throw new IllegalArgumentException("You cannot assign a challenge to yourself. Use the standard 'Start' button instead.");
        }

        boolean isAlreadyActive = repository.isChallengeActiveForUser(
                request.getUserId(),
                request.getChallengeId(),
                Arrays.asList(ChallengeUserStatus.PENDING, ChallengeUserStatus.ACCEPTED)
        );

        if (isAlreadyActive) {
            throw new ConflictException("You have already sent this challenge to this friend.");
        }

        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Target user not found"));
        Challenge challenge = challengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new ChallengeNotFoundException(request.getChallengeId()));

        Optional<ChallengeUser> existingLinkOpt = repository.findByUserIdAndChallengeId(targetUser.getId(), challenge.getId());
        ChallengeUser linkToSave;

        if (existingLinkOpt.isPresent()) {

            linkToSave = existingLinkOpt.get();
            linkToSave.setStatus(ChallengeUserStatus.PENDING);
            linkToSave.setAssignedBy(currentUser.getId());

            linkToSave.setDateAccepted(null);
            linkToSave.setDateCompleted(null);
            linkToSave.setStartDate(null);
            linkToSave.setDeadline(null);
        } else {
            linkToSave = new ChallengeUser();
            linkToSave.setUser(targetUser);
            linkToSave.setChallenge(challenge);
            linkToSave.setAssignedBy(currentUser.getId());
            linkToSave.setStatus(ChallengeUserStatus.PENDING);
        }

        ChallengeUser savedLink = repository.save(linkToSave);

        // Notify the target friend
        notificationService.createNotification(new NotificationCreateRequest(
                targetUser.getId(),
                currentUser.getUsername() + " challenged you to: " + challenge.getTitle() + "!",
                NotificationType.CHALLENGE
        ));

        return savedLink;
    }

    /**
     * Updates challenge progress. If status is COMPLETED, it triggers:
     * 1. XP Reward calculation.
     * 2. Global mission counter increment.
     * 3. Social notification back to the assigner.
     */
    @Transactional
    public ChallengeUser updateChallengeUserStatus(UUID id, ChallengeUserUpdateRequest request) {
        ChallengeUser link = getChallengeUserById(id);
        ChallengeUserStatus oldStatus = link.getStatus();

        if (request.getStatus() != null) link.setStatus(request.getStatus());

        // Handle Acceptance Phase
        if (request.getStatus() == ChallengeUserStatus.ACCEPTED) {
            if (link.getDateAccepted() == null) {
                link.setDateAccepted(ZonedDateTime.now(ZoneId.of("Europe/Bucharest")).toLocalDateTime());
            }
            if (request.getStartDate() != null) link.setStartDate(request.getStartDate());
            if (request.getTargetDeadline() != null) link.setDeadline(request.getTargetDeadline());


            if (oldStatus != ChallengeUserStatus.ACCEPTED) {

                if (link.getAssignedBy() != null && !link.getAssignedBy().equals(link.getUser().getId())) {

                    String message = "Game on! " + link.getUser().getUsername() + " accepted your challenge: " + link.getChallenge().getTitle();

                    notificationService.createNotification(new NotificationCreateRequest(
                            link.getAssignedBy(),
                            message,
                            NotificationType.CHALLENGE
                    ));
                }
            }
        }
        // Handle Completion Phase (Rewards Logic)
        if (request.getStatus() == ChallengeUserStatus.COMPLETED) {
            if (link.getDateAccepted() == null) link.setDateAccepted(ZonedDateTime.now(ZoneId.of("Europe/Bucharest")).toLocalDateTime());
            link.setDateCompleted(ZonedDateTime.now(ZoneId.of("Europe/Bucharest")).toLocalDateTime());

            if (oldStatus != ChallengeUserStatus.COMPLETED) {

                Integer currentTimes = link.getTimesCompleted();
                link.setTimesCompleted(currentTimes == null ? 1 : currentTimes + 1);

                User user = link.getUser();
                Challenge challenge = link.getChallenge();

                int pointsToAdd = challenge.getPoints();
                int currentXP = (user.getPoints() == null) ? 0 : user.getPoints();
                user.setPoints(currentXP + pointsToAdd);

                int totalMissions = (user.getTotalCompletedChallenges() == null) ? 0 : user.getTotalCompletedChallenges();
                user.setTotalCompletedChallenges(totalMissions + 1);

                userRepository.save(user);

                // Social Victory Notification
                if (link.getAssignedBy() != null && !link.getAssignedBy().equals(link.getUser().getId())) {
                    String message = "Victory! " + link.getUser().getUsername() +
                            " has crushed your challenge: " + link.getChallenge().getTitle() +
                            " (+" + link.getChallenge().getPoints() + " XP)!";

                    notificationService.createNotification(new NotificationCreateRequest(
                            link.getAssignedBy(),
                            message,
                            NotificationType.CHALLENGE
                    ));
                }
            }


        }
        return repository.save(link);
    }

    /**
     * Deletes a participation record. Sends a 'Refusal' notification if a friend's assignment is declined.
     */
    @Transactional
    public void deleteChallengeUser(UUID id) {
        ChallengeUser link = getChallengeUserById(id);

        if (link.getAssignedBy() != null && !link.getAssignedBy().equals(link.getUser().getId())) {
            if (link.getStatus() == ChallengeUserStatus.PENDING || link.getStatus() == ChallengeUserStatus.RECEIVED) {

                String message = link.getUser().getUsername() +
                        " has declined your challenge: " + link.getChallenge().getTitle() +
                        ". Maybe next time!";

                notificationService.createNotification(new NotificationCreateRequest(
                        link.getAssignedBy(),
                        message,
                        NotificationType.CHALLENGE
                ));
            }
        }

        repository.delete(link);
    }

    // Helper methods...
    public List<ChallengeUserDTO> getChallengeUsersByStatus(UUID userId, String statusString) {
        ChallengeUserStatus status = ChallengeUserStatus.valueOf(statusString.toUpperCase());
        return repository.findByUserIdAndStatus(userId, status).stream().map(this::convertToDto).toList();
    }
}