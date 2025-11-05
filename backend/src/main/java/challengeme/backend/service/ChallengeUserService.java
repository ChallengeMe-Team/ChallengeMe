package challengeme.backend.service;

import challengeme.backend.exception.ChallengeUserNotFoundException;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.ChallengeUserStatus;
import challengeme.backend.model.CreateChallengeUserRequest;
import challengeme.backend.repository.ChallengeUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeUserService {

    private final ChallengeUserRepository challengeUserRepository;
    // Se pot injecta și UserService și ChallengeService aici pentru validare
    // private final UserService userService;
    // private final ChallengeService challengeService;

    public ChallengeUser createChallengeUser(CreateChallengeUserRequest request) {
        if (request.getUserId() == null || request.getChallengeId() == null) {
            throw new IllegalArgumentException("User ID and Challenge ID cannot be null");
        }

        // Validare (simulată): Verifică dacă User-ul și Challenge-ul există
        // În mod real, se apeleaza:
        // userService.getUserById(request.getUserId());
        // challengeService.getChallengeById(request.getChallengeId());

        ChallengeUser newChallengeUser = new ChallengeUser();
        newChallengeUser.setUserId(request.getUserId());
        newChallengeUser.setChallengeId(request.getChallengeId());
        newChallengeUser.setStatus(ChallengeUserStatus.PENDING); // Status inițial

        return challengeUserRepository.save(newChallengeUser);
    }

    public List<ChallengeUser> getAllChallengeUsers() {
        return challengeUserRepository.findAll();
    }

    public ChallengeUser getChallengeUserById(UUID id) {
        return challengeUserRepository.findById(id)
                .orElseThrow(() -> new ChallengeUserNotFoundException("ChallengeUser link not found with id: " + id));
    }

    public List<ChallengeUser> getChallengeUsersByUserId(UUID userId) {
        return challengeUserRepository.findByUserId(userId);
    }

    public ChallengeUser updateChallengeUserStatus(UUID id, ChallengeUserStatus newStatus) {
        ChallengeUser challengeUser = getChallengeUserById(id);
        challengeUser.setStatus(newStatus);
        if (newStatus == ChallengeUserStatus.ACCEPTED) {
            challengeUser.setDateAccepted(LocalDate.now());
        } else if (newStatus == ChallengeUserStatus.COMPLETED) {
            if (challengeUser.getDateAccepted() == null) {
                challengeUser.setDateAccepted(LocalDate.now()); // Setează și data acceptării dacă e cazul
            }
            challengeUser.setDateCompleted(LocalDate.now());
        }
        return challengeUserRepository.save(challengeUser);
    }

    public void deleteChallengeUser(UUID id) {
        // Verifică dacă există înainte de a șterge
        if (challengeUserRepository.findById(id).isEmpty()) {
            throw new ChallengeUserNotFoundException("ChallengeUser link not found with id: " + id);
        }
        challengeUserRepository.deleteById(id);
    }
}
