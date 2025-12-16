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

    public ChallengeUser updateChallengeUserStatus(UUID id, ChallengeUserUpdateRequest request) {
        ChallengeUser link = getChallengeUserById(id);

        link.setStatus(request.getStatus());

        if (request.getStatus() == ChallengeUserStatus.ACCEPTED) {
            if (link.getDateAccepted() == null) {
                link.setDateAccepted(LocalDate.now());
            }
            if (request.getStartDate() != null) link.setStartDate(request.getStartDate());
            if (request.getDeadline() != null) link.setDeadline(request.getDeadline());
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
