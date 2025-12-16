package challengeme.backend.service;

import challengeme.backend.dto.request.create.ChallengeUserCreateRequest;
import challengeme.backend.dto.request.create.NotificationCreateRequest;
import challengeme.backend.dto.request.update.ChallengeUserUpdateRequest;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.exception.ChallengeUserNotFoundException;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.model.*;
import challengeme.backend.repository.ChallengeRepository;
import challengeme.backend.repository.ChallengeUserRepository;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException; // <--- Import necesar pentru erori

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

    // Metoda simplă de creare (admin sau direct)
    public ChallengeUser createChallengeUser(ChallengeUserCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));
        Challenge challenge = challengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new ChallengeNotFoundException(request.getChallengeId()));

        ChallengeUser link = new ChallengeUser();
        link.setUser(user);
        link.setChallenge(challenge);
        link.setStatus(ChallengeUserStatus.PENDING);

        return repository.save(link);
    }

    public List<ChallengeUser> getAllChallengeUsers() {
        return repository.findAll();
    }

    public ChallengeUser getChallengeUserById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ChallengeUserNotFoundException("ChallengeUser not found with id: " + id));
    }

    public List<ChallengeUser> getChallengeUsersByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    // --- AICI SUNT MODIFICĂRILE PENTRU TASK-UL DE TRIMITERE ---
    @Transactional
    public ChallengeUser assignChallenge(ChallengeUserCreateRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));

        // 1. Validare: Self-Challenge Prevention
        // Nu te poți provoca singur prin acest meniu
        if (currentUser.getId().equals(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot assign a challenge to yourself. Use the standard 'Start' button instead.");
        }

        // 2. Validare: Duplicate Spam Protection
        // Verificăm dacă ai mai trimis deja acest challenge acestui user
        if (repository.existsByUserIdAndChallengeId(request.getUserId(), request.getChallengeId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You have already sent this challenge to this friend.");
        }

        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Target user not found with id: " + request.getUserId()));

        Challenge challenge = challengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new ChallengeNotFoundException(request.getChallengeId()));

        // Salvarea link-ului
        ChallengeUser link = new ChallengeUser();
        link.setUser(targetUser);
        link.setChallenge(challenge);
        link.setAssignedBy(currentUser.getId());
        link.setStatus(ChallengeUserStatus.RECEIVED); // Status inițial corect pentru o provocare primită

        ChallengeUser savedLink = repository.save(link);

        // Notificare către cel provocat (Target)
        String message = currentUser.getUsername() + " te-a provocat la: " + challenge.getTitle() + "!";
        NotificationCreateRequest notifRequest = new NotificationCreateRequest(
                targetUser.getId(),
                message,
                NotificationType.CHALLENGE
        );
        notificationService.createNotification(notifRequest);

        return savedLink;
    }

    // --- AICI SUNT MODIFICĂRILE PENTRU TASK-UL DE ACCEPTARE ---
    public ChallengeUser updateChallengeUserStatus(UUID id, ChallengeUserUpdateRequest request) {
        ChallengeUser link = getChallengeUserById(id);

        // Verificăm dacă statusul se schimbă în ACCEPTED acum (pentru a nu trimite notificări duble)
        boolean isJustAccepted = request.getStatus() == ChallengeUserStatus.ACCEPTED
                && link.getStatus() != ChallengeUserStatus.ACCEPTED;

        link.setStatus(request.getStatus());

        if (request.getStatus() == ChallengeUserStatus.ACCEPTED) {
            if (link.getDateAccepted() == null) {
                link.setDateAccepted(LocalDate.now());
            }
            if (request.getStartDate() != null) link.setStartDate(request.getStartDate());
            if (request.getDeadline() != null) link.setDeadline(request.getDeadline());

            // 3. Sender Feedback Loop (Notificarea inversă)
            // Dacă provocarea a fost trimisă de cineva (assignedBy nu e null), îi dăm de veste
            if (isJustAccepted && link.getAssignedBy() != null) {
                String friendName = link.getUser().getUsername();
                String challengeTitle = link.getChallenge().getTitle();

                String feedbackMessage = "Game on! " + friendName + " has accepted your challenge: " + challengeTitle + ".";

                NotificationCreateRequest feedbackNotif = new NotificationCreateRequest(
                        link.getAssignedBy(), // Trimitem înapoi la Sender
                        feedbackMessage,
                        NotificationType.SYSTEM // Sau CHALLENGE, cum preferi
                );
                notificationService.createNotification(feedbackNotif);
            }
        }

        if (request.getStatus() == ChallengeUserStatus.COMPLETED) {
            if (link.getDateAccepted() == null) link.setDateAccepted(LocalDate.now()); // Safety check
            link.setDateCompleted(LocalDate.now());
        }

        return repository.save(link);
    }

    public void deleteChallengeUser(UUID id) {
        ChallengeUser link = getChallengeUserById(id);
        repository.delete(link);
    }
}