package challengeme.backend.service;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.dto.request.create.NotificationCreateRequest;
import challengeme.backend.dto.request.update.ChallengeUpdateRequest;
import challengeme.backend.dto.request.update.UpdateChallengeRequest;
import challengeme.backend.mapper.ChallengeMapper;
import challengeme.backend.model.*;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.repository.ChallengeRepository;
import challengeme.backend.repository.ChallengeUserRepository;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Service responsible for managing challenge definitions and their progress tracking.
 * It handles CRUD operations for challenges, ownership validation, and the
 * rewarding logic for experience points (XP) upon completion.
 */
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository repository;
    private final ChallengeUserRepository challengeUserRepository;
    private final ChallengeMapper mapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /** Retrieves all challenges available in the global catalog. */
    public List<Challenge> getAllChallenges() {
        return repository.findAll();
    }

    /**
     * Finds a challenge by its UUID.
     * @param id Challenge identifier.
     * @throws ChallengeNotFoundException if the record is missing.
     */
    public Challenge getChallengeById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ChallengeNotFoundException(id));
    }

    /** Lists challenges created by a specific user. */
    public List<Challenge> getChallengesByCreator(String username) {
        return repository.findAllByCreatedBy(username);
    }

    /** Persists a new challenge definition. */
    public Challenge addChallenge(Challenge challenge) {
        return repository.save(challenge);
    }

    /**
     * Updates an existing challenge.
     * Includes an ownership check to ensure only the creator can modify the quest.
     * @param id ID of the challenge to update.
     * @param request Update details.
     */
    public Challenge updateChallenge(UUID id, ChallengeUpdateRequest request) {
        Challenge entity = getChallengeById(id);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!entity.getCreatedBy().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own challenges.");
        }
        mapper.updateEntity(request, entity);
        return repository.save(entity);
    }

    /**
     * Permanently deletes a challenge and all its associated user participations.
     * Validates ownership before execution.
     */
    @Transactional
    public void deleteChallenge(UUID id) {
        Challenge entity = getChallengeById(id);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!entity.getCreatedBy().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own challenges.");
        }

        challengeUserRepository.deleteAllByChallengeId(entity.getId());

        repository.delete(entity);
    }

    /**
     * Synchronizes the creator's username across all challenges if a user updates their profile.
     * Ensures data consistency without breaking foreign key logic.
     */
    @Transactional
    public void synchronizeUsername(String oldUsername, String newUsername) {
        repository.updateCreatorUsername(oldUsername, newUsername);
    }

    /**
     * Core business logic for status transitions (ACCEPTED, COMPLETED).
     * If COMPLETED: Increments timesCompleted, awards XP points, and notifies the assigner.
     * If ACCEPTED: Sets start timestamps and notifies the assigner.
     * @param id Participation ID (ChallengeUser).
     * @param request The status update details.
     * @return Updated ChallengeUserDTO.
     */
    @Transactional
    public ChallengeUserDTO updateStatus(UUID id, UpdateChallengeRequest request) {
        ChallengeUser link = challengeUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Link not found"));

        ChallengeUserStatus oldStatus = link.getStatus();
        ChallengeUserStatus newStatus = ChallengeUserStatus.valueOf(request.getStatus());

        // 1. Completion Logic
        if (newStatus == ChallengeUserStatus.COMPLETED && oldStatus != ChallengeUserStatus.COMPLETED) {
            link.setDateCompleted(ZonedDateTime.now(ZoneId.of("Europe/Bucharest")).toLocalDateTime());
            UUID userId = link.getUser().getId();
            Integer earnedXP = link.getChallenge().getPoints();

            int currentCount = link.getTimesCompleted() != null ? link.getTimesCompleted() : 0;
            link.setTimesCompleted(currentCount + 1);

            userRepository.incrementMissionsAndXP(userId, earnedXP);

             sendFriendNotification(link, "Victory! " + link.getUser().getUsername() +
                    " has crushed the challenge you sent: " + link.getChallenge().getTitle());
        }

        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Bucharest")).toLocalDateTime();

        // 2. Acceptance Logic
        if (newStatus == ChallengeUserStatus.ACCEPTED && oldStatus != ChallengeUserStatus.ACCEPTED) {
            link.setDateAccepted(now);
            link.setStartDate(now);

            sendFriendNotification(link, "Game on! " + link.getUser().getUsername() +
                    " accepted your challenge: " + link.getChallenge().getTitle());
        }

        link.setStatus(newStatus);

        if (request.getStartDate() != null && newStatus != ChallengeUserStatus.ACCEPTED) {
            link.setStartDate(request.getStartDate().atStartOfDay());
        }

        if (request.getTargetDeadline() != null) {
            link.setDeadline(request.getTargetDeadline());
        }

        var saved = challengeUserRepository.save(link);
        return convertToDto(saved);
    }

    /**
     * Helper to send social notifications to the friend who assigned the challenge.
     */
    private void sendFriendNotification(ChallengeUser link, String message) {
        if (link.getAssignedBy() != null && !link.getAssignedBy().equals(link.getUser().getId())) {
            notificationService.createNotification(new NotificationCreateRequest(
                    link.getAssignedBy(),
                    message,
                    NotificationType.CHALLENGE
            ));
        }
    }

    /** Internal mapping logic to convert participation entity to DTO. */
    private ChallengeUserDTO convertToDto(ChallengeUser entity) {
        ChallengeUserDTO dto = new ChallengeUserDTO();

        dto.setId(entity.getId());

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
        }

        if (entity.getChallenge() != null) {
            dto.setChallengeId(entity.getChallenge().getId());
            dto.setChallengeTitle(entity.getChallenge().getTitle());
        }

        dto.setStatus(entity.getStatus());

        dto.setDateAccepted(entity.getDateAccepted());
        dto.setDateCompleted(entity.getDateCompleted());
        dto.setTimesCompleted(entity.getTimesCompleted());

        System.out.println("DEBUG MAPPER - dateAccepted trimis în DTO: " + dto.getDateAccepted());

        return dto;
    }
}