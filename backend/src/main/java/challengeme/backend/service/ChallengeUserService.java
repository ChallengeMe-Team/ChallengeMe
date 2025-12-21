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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeUserService {

    private final ChallengeUserRepository repository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final NotificationService notificationService;

    // Metoda de creare standard (ex: butonul Start)
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
            challengeUser.setStartDate(request.getStartDate());
            challengeUser.setDateAccepted(LocalDate.now());
        }
        if (request.getTargetDeadline() != null) {
            challengeUser.setDeadline(request.getTargetDeadline());
        }
        challengeUser.setAssignedBy(user.getId());
        ChallengeUser saved = repository.save(challengeUser);
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
        dto.setDateAccepted(entity.getDateAccepted());
        dto.setDateCompleted(entity.getDateCompleted());
        dto.setDeadline(entity.getDeadline());
        return dto;
    }

    public List<ChallengeUser> getChallengeUsersByUserId(UUID userId) { return repository.findByUserId(userId); }

    // --- LOGICA DE ASSIGN CU MESAJELE EXACTE DIN TICHET ---
    @Transactional
    public ChallengeUser assignChallenge(ChallengeUserCreateRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));

        // 1. Validare Self-Assign (Mesaj Corectat)
        if (currentUser.getId().equals(request.getUserId())) {
            throw new IllegalArgumentException("You cannot assign a challenge to yourself. Use the standard 'Start' button instead.");
        }

        // 2. Validare Spam (Mesaj Corectat)
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
            // Update la vechiul challenge
            linkToSave = existingLinkOpt.get();
            linkToSave.setStatus(ChallengeUserStatus.PENDING);
            linkToSave.setAssignedBy(currentUser.getId());

            linkToSave.setDateAccepted(null);
            linkToSave.setDateCompleted(null);
            linkToSave.setStartDate(null);
            linkToSave.setDeadline(null);
        } else {
            // Creare noua
            linkToSave = new ChallengeUser();
            linkToSave.setUser(targetUser);
            linkToSave.setChallenge(challenge);
            linkToSave.setAssignedBy(currentUser.getId());
            linkToSave.setStatus(ChallengeUserStatus.PENDING);
        }

        ChallengeUser savedLink = repository.save(linkToSave);

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

        if (request.getStatus() != null) link.setStatus(request.getStatus());

        if (request.getStatus() == ChallengeUserStatus.ACCEPTED) {
            if (link.getDateAccepted() == null) {
                link.setDateAccepted(LocalDate.now());
            }
            if (request.getStartDate() != null) link.setStartDate(request.getStartDate());
            if (request.getTargetDeadline() != null) link.setDeadline(request.getTargetDeadline());

            // --- FIX START ---
            // 1. Verificăm "oldStatus" ca să nu trimitem notificare dacă userul doar își actualizează datele
            if (oldStatus != ChallengeUserStatus.ACCEPTED) {

                // 2. Verificăm dacă a fost asignat de altcineva (nu self-challenge)
                if (link.getAssignedBy() != null && !link.getAssignedBy().equals(link.getUser().getId())) {
                    User sender = userRepository.findById(link.getAssignedBy()).orElse(null);

                    if (sender != null) {
                        // 3. Mesajul CORECT de acceptare
                        String message = "Game on! " + link.getUser().getUsername() + " a acceptat provocarea ta: " + link.getChallenge().getTitle();

                        NotificationCreateRequest notifRequest = new NotificationCreateRequest(
                                sender.getId(),
                                message,
                                NotificationType.CHALLENGE // Sau SYSTEM, cum preferi
                        );
                        notificationService.createNotification(notifRequest);
                    }
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

                // --- NOTIFICARE COMPLETION START ---
                // Verificăm dacă a fost o provocare trimisă de altcineva
                if (link.getAssignedBy() != null && !link.getAssignedBy().equals(link.getUser().getId())) {
                    String message = "Victory! " + link.getUser().getUsername() +
                            " has crushed your challenge: " + link.getChallenge().getTitle() +
                            " (+" + link.getChallenge().getPoints() + " XP)!";

                    notificationService.createNotification(new NotificationCreateRequest(
                            link.getAssignedBy(),
                            message,
                            NotificationType.CHALLENGE // Folosim CHALLENGE pentru tematică unitară
                    ));
                }
            }


        }
        return repository.save(link);
    }

    @Transactional
    public void deleteChallengeUser(UUID id) {
        ChallengeUser link = getChallengeUserById(id);

        // --- NOTIFICARE REFUSAL START ---
        // Dacă provocarea a fost trimisă de altcineva și este încă în stadiul PENDING/RECEIVED
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
        // --- NOTIFICARE REFUSAL END ---

        repository.delete(link);
    }

    public List<ChallengeUserDTO> getChallengeUsersByStatus(UUID userId, String statusString) {
        ChallengeUserStatus status = ChallengeUserStatus.valueOf(statusString.toUpperCase());
        return repository.findByUserIdAndStatus(userId, status).stream().map(this::convertToDto).toList();
    }
}