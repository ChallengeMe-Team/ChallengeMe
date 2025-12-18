package challengeme.backend.service;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.dto.request.create.ChallengeUserCreateRequest;
import challengeme.backend.dto.request.create.NotificationCreateRequest;
import challengeme.backend.dto.request.update.ChallengeUserUpdateRequest;
import challengeme.backend.dto.request.update.UpdateChallengeRequest;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.exception.ChallengeUserNotFoundException;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.model.*;
import challengeme.backend.repository.ChallengeRepository;
import challengeme.backend.repository.ChallengeUserRepository;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ChallengeUserDTO acceptChallenge(UUID challengeId, String username, UpdateChallengeRequest request) {
        // 1. Găsim User-ul (presupunând că ai userRepository)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Găsim Challenge-ul
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        // 3. Verificăm dacă userul are deja această provocare (opțional, dar recomandat)
        // boolean exists = userChallengeRepository.existsByUserAndChallenge(user, challenge);
        // if (exists) throw new RuntimeException("You already accepted this challenge!");

        // 4. Creăm relația nouă
        ChallengeUser challengeUser = new ChallengeUser();
        challengeUser.setUser(user);
        challengeUser.setChallenge(challenge);
        challengeUser.setStatus(ChallengeUserStatus.valueOf(request.getStatus())); // status din Frontend

        // Setăm datele din Request
        if (request.getStartDate() != null) {
            challengeUser.setStartDate(request.getStartDate());
            challengeUser.setDateAccepted(request.getStartDate()); // Setăm și data acceptării
        }

        if (request.getTargetDeadline() != null) {
            // ATENȚIE: Verifică dacă entitatea ta are 'deadline' sau 'targetDeadline'
            challengeUser.setDeadline(request.getTargetDeadline());
        }

        // IMPORTANT: Setăm assignedBy (ca să nu crape baza de date cu NotNull)
        // Fiind self-assigned, punem ID-ul userului propriu
        challengeUser.setAssignedBy(user.getId());

        // 5. Salvăm
        ChallengeUser saved = repository.save(challengeUser);

        return convertToDto(saved);
    }

    private ChallengeUserDTO convertToDto(ChallengeUser entity) {
        ChallengeUserDTO dto = new ChallengeUserDTO();

        // Mapăm ID-ul relației
        dto.setId(entity.getId());

        // Mapăm datele despre User (verificăm să nu fie null)
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
        }

        // Mapăm datele despre Challenge
        if (entity.getChallenge() != null) {
            dto.setChallengeId(entity.getChallenge().getId());
            dto.setChallengeTitle(entity.getChallenge().getTitle());
        }

        // Mapăm statusul și datele calendaristice
        dto.setStatus(entity.getStatus());
        dto.setDateAccepted(entity.getDateAccepted());
        dto.setDateCompleted(entity.getDateCompleted());

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

        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Target user not found with id: " + request.getUserId()));

        Challenge challenge = challengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new ChallengeNotFoundException(request.getChallengeId()));

        ChallengeUser link = new ChallengeUser();
        link.setUser(targetUser);
        link.setChallenge(challenge);
        link.setAssignedBy(currentUser.getId());
        link.setStatus(ChallengeUserStatus.PENDING);

        ChallengeUser savedLink = repository.save(link);

        String message = currentUser.getUsername() + " te-a provocat la: " + challenge.getTitle() + "!";

        NotificationCreateRequest notifRequest = new NotificationCreateRequest(
                targetUser.getId(),
                message,
                NotificationType.CHALLENGE
        );

        notificationService.createNotification(notifRequest);

        return savedLink;
    }

    @Transactional
    public ChallengeUser updateChallengeUserStatus(UUID id, ChallengeUserUpdateRequest request) {
        ChallengeUser link = getChallengeUserById(id);

        ChallengeUserStatus oldStatus = link.getStatus();

        link.setStatus(request.getStatus());

        if (request.getStatus() == ChallengeUserStatus.ACCEPTED) {
            if (link.getDateAccepted() == null) {
                link.setDateAccepted(LocalDate.now());
            }
            if (request.getStartDate() != null) link.setStartDate(request.getStartDate());
            if (request.getDeadline() != null) link.setDeadline(request.getDeadline());
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
}