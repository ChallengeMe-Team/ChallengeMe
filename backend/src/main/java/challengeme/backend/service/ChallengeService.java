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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository repository;
    private final ChallengeUserRepository challengeUserRepository;
    private final ChallengeMapper mapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public List<Challenge> getAllChallenges() {
        return repository.findAll();
    }

    public Challenge getChallengeById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ChallengeNotFoundException(id));
    }

    public List<Challenge> getChallengesByCreator(String username) {
        return repository.findAllByCreatedBy(username);
    }

    public Challenge addChallenge(Challenge challenge) {
        return repository.save(challenge);
    }

    public Challenge updateChallenge(UUID id, ChallengeUpdateRequest request) {
        Challenge entity = getChallengeById(id);

        // --- Ownership Check ---
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!entity.getCreatedBy().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own challenges.");
        }
        mapper.updateEntity(request, entity);
        return repository.save(entity);
    }

    @Transactional
    public void deleteChallenge(UUID id) {
        //Verificam existenta
        Challenge entity = getChallengeById(id);

        //Verificam Ownership-ul
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!entity.getCreatedBy().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own challenges.");
        }

        //Stergem dependentele
        challengeUserRepository.deleteAllByChallengeId(entity.getId());

        //Stergem Challenge-ul propriu-zis
        repository.delete(entity);
    }

    @Transactional
    public void synchronizeUsername(String oldUsername, String newUsername) {
        // Aceasta va executa query-ul creat mai sus
        repository.updateCreatorUsername(oldUsername, newUsername);
    }

    @Transactional
    public ChallengeUserDTO updateStatus(UUID id, UpdateChallengeRequest request) {
        ChallengeUser link = challengeUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Link not found"));

        ChallengeUserStatus oldStatus = link.getStatus();
        ChallengeUserStatus newStatus = ChallengeUserStatus.valueOf(request.getStatus());

        // 1. Logica pentru FINISH (COMPLETED)
        if (newStatus == ChallengeUserStatus.COMPLETED && oldStatus != ChallengeUserStatus.COMPLETED) {
            link.setDateCompleted(LocalDateTime.now());
            UUID userId = link.getUser().getId();
            Integer earnedXP = link.getChallenge().getPoints();

            // Incrementăm repetările pentru această provocare specifică
            int currentCount = link.getTimes_completed() != null ? link.getTimes_completed() : 0;
            link.setTimes_completed(currentCount + 1);

            // Actualizăm XP-ul și misiunile globale în tabelul Users
            userRepository.incrementMissionsAndXP(userId, earnedXP);

            // NOTIFICARE VICTORY: Trimitem către cel care a dat provocarea (ex: Calin)
            sendFriendNotification(link, "Victory! " + link.getUser().getUsername() +
                    " has crushed the challenge you sent: " + link.getChallenge().getTitle());
        }

        // 2. Logica pentru ACCEPTARE
        if (newStatus == ChallengeUserStatus.ACCEPTED && oldStatus != ChallengeUserStatus.ACCEPTED) {
            link.setDateAccepted(LocalDate.now());

            // NOTIFICARE ACCEPTARE: Trimitem către prieten
            sendFriendNotification(link, "Game on! " + link.getUser().getUsername() +
                    " accepted your challenge: " + link.getChallenge().getTitle());
        }

        // Actualizăm statusul și restul câmpurilor din request
        link.setStatus(newStatus);

        if (request.getStartDate() != null) {
            link.setStartDate(request.getStartDate()); // Asigură-te că folosești setStartDate pentru LocalDate
        }

        if (request.getTargetDeadline() != null) {
            link.setDeadline(request.getTargetDeadline());
        }

        var saved = challengeUserRepository.save(link);
        return convertToDto(saved);
    }

    // Metodă Helper pentru a nu repeta codul
    private void sendFriendNotification(ChallengeUser link, String message) {
        if (link.getAssignedBy() != null && !link.getAssignedBy().equals(link.getUser().getId())) {
            notificationService.createNotification(new NotificationCreateRequest(
                    link.getAssignedBy(),
                    message,
                    NotificationType.CHALLENGE
            ));
        }
    }

    private ChallengeUserDTO convertToDto(ChallengeUser entity) {
        ChallengeUserDTO dto = new ChallengeUserDTO();

        // Mapare ID-uri
        dto.setId(entity.getId());

        // Mapare User (presupunând că ai relația @ManyToOne către User)
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

        return dto;
    }
}