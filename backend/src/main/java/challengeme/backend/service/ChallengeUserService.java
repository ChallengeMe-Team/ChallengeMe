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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeUserService {

    private final ChallengeUserRepository repository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final NotificationService notificationService;

    public ChallengeUser createChallengeUser(ChallengeUserCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));
        Challenge challenge = challengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new ChallengeNotFoundException(request.getChallengeId()));

        ChallengeUser link = new ChallengeUser();
        link.setUser(user);
        link.setChallenge(challenge);
        link.setStatus(ChallengeUserStatus.PENDING);
        // Note: Logic for 'assignedBy' on self-creation might be needed here depending on business rules,
        // usually defaults to the user themselves or null/system.
        link.setAssignedBy(user.getId());

        return repository.save(link);
    }

    public List<ChallengeUser> getAllChallengeUsers() {
        return repository.findAll();
    }

    public ChallengeUser getChallengeUserById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ChallengeUserNotFoundException("ChallengeUser not found with id: " + id));
    }

    @Transactional
    public ChallengeUserDTO acceptChallenge(UUID challengeId, String username, UpdateChallengeRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ChallengeNotFoundException(challengeId));

        // Validare - Nu poți da "Start" dacă ai deja provocarea în orice stare (Received, Active, etc.)
        if (repository.existsByUserIdAndChallengeId(user.getId(), challenge.getId())) {
            throw new ConflictException("You already have this challenge in your list or inbox.");
        }

        ChallengeUser challengeUser = new ChallengeUser();
        challengeUser.setUser(user);
        challengeUser.setChallenge(challenge);
        challengeUser.setStatus(ChallengeUserStatus.valueOf(request.getStatus()));

        if (request.getStartDate() != null) {
            challengeUser.setStartDate(request.getStartDate());
            challengeUser.setDateAccepted(LocalDate.now());
        }

        if (request.getTargetDeadline() != null) {
            challengeUser.setDeadline(request.getTargetDeadline());
        }

        challengeUser.setAssignedBy(user.getId()); // Self-assigned
        ChallengeUser saved = repository.save(challengeUser);

        return convertToDto(saved);
    }

    private ChallengeUserDTO convertToDto(ChallengeUser entity) {
        ChallengeUserDTO dto = new ChallengeUserDTO();

        // 1. Mapăm ID-ul relației
        dto.setId(entity.getId());

        // 2. Mapăm datele despre User
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
        }

        // 3. Mapăm datele despre Challenge (AICI ERA LIPSA)
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

        // 4. Mapăm statusul și datele calendaristice
        dto.setStatus(entity.getStatus());
        dto.setDateAccepted(entity.getDateAccepted());
        dto.setDateCompleted(entity.getDateCompleted());
        dto.setDeadline(entity.getDeadline()); // Mapăm și deadline-ul

        return dto;
    }

    public List<ChallengeUser> getChallengeUsersByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    @Transactional
    public ChallengeUser assignChallenge(ChallengeUserCreateRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));

        if (currentUser.getId().equals(request.getUserId())) {
            // Poți folosi IllegalArgumentException care se mapează uzual la 400 Bad Request
            throw new IllegalArgumentException("You cannot assign a challenge to yourself. Use the standard 'Start' button instead.");
        }

        System.out.println("Assigning challenge " + request.getChallengeId() + " to user " + request.getUserId());
        if (repository.existsByUserIdAndChallengeId(request.getUserId(), request.getChallengeId())) {
            throw new ConflictException("You already sent this challenge to this friend");
        }

        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Target user not found"));

        Challenge challenge = challengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new ChallengeNotFoundException(request.getChallengeId()));

        ChallengeUser link = new ChallengeUser();
        link.setUser(targetUser);
        link.setChallenge(challenge);
        link.setAssignedBy(currentUser.getId());
        link.setStatus(ChallengeUserStatus.PENDING);

        ChallengeUser savedLink = repository.save(link);

        // 2. Notificare
        notificationService.createNotification(new NotificationCreateRequest(
                targetUser.getId(),
                currentUser.getUsername() + " te-a provocat la: " + challenge.getTitle() + "!",
                NotificationType.CHALLENGE
        ));

        return savedLink;
    }

    @Transactional
    public ChallengeUser updateChallengeUserStatus(UUID id, ChallengeUserUpdateRequest request) {
        ChallengeUser link = getChallengeUserById(id);
        ChallengeUserStatus oldStatus = link.getStatus();

        // MODIFICARE 1: Verificăm dacă statusul e null înainte să-l setăm
        if (request.getStatus() != null) {
            link.setStatus(request.getStatus());
        }

        // Dacă noul status este ACCEPTED
        if (request.getStatus() == ChallengeUserStatus.ACCEPTED) {
            // Setăm data acceptării la ziua curentă
            link.setDateAccepted(LocalDate.now());

            if (request.getStartDate() != null) {
                link.setStartDate(request.getStartDate());
            }

            // MODIFICARE 2: Folosim getTargetDeadline() în loc de getDeadline()
            // (pentru a se potrivi cu modificarea făcută în DTO)
            if (request.getTargetDeadline() != null) {
                link.setDeadline(request.getTargetDeadline());
            }

            // Sender Feedback Loop (Notificare către cel care a trimis provocarea)
            if (link.getAssignedBy() != null && !link.getAssignedBy().equals(link.getUser().getId())) {
                User sender = userRepository.findById(link.getAssignedBy()).orElse(null);
                if (sender != null) {
                    notificationService.createNotification(new NotificationCreateRequest(
                            sender.getId(),
                            "Game on! " + link.getUser().getUsername() + " has accepted your challenge: " + link.getChallenge().getTitle() + ".",
                            NotificationType.CHALLENGE
                    ));
                }
            }
        }

        if (request.getStatus() == ChallengeUserStatus.COMPLETED) {
            if (link.getDateAccepted() == null) link.setDateAccepted(LocalDate.now());
            link.setDateCompleted(LocalDate.now());

            if (oldStatus != ChallengeUserStatus.COMPLETED) {
                User user = link.getUser();
                Challenge challenge = link.getChallenge();

                int pointsReward = challenge.getPoints();
                int currentPoints = user.getPoints() == null ? 0 : user.getPoints();

                user.setPoints(currentPoints + pointsReward);
                userRepository.save(user);
            }
        }

        return repository.save(link);
    }

    public void deleteChallengeUser(UUID id) {
        ChallengeUser link = getChallengeUserById(id);
        repository.delete(link);
    }

    public List<ChallengeUserDTO> getChallengeUsersByStatus(UUID userId, String statusString) {
        // Convertim string-ul primit din URL (ex: "RECEIVED") în Enum
        ChallengeUserStatus status = ChallengeUserStatus.valueOf(statusString.toUpperCase());

        List<ChallengeUser> challenges = repository.findByUserIdAndStatus(userId, status);

        return challenges.stream()
                .map(this::convertToDto)
                .toList();
    }
}