package challengeme.backend.service;

import challengeme.backend.dto.request.create.ChallengeUserCreateRequest;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.exception.ChallengeUserNotFoundException;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.model.Challenge;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.ChallengeUserStatus;
import challengeme.backend.model.User;
import challengeme.backend.repository.ChallengeRepository;
import challengeme.backend.repository.ChallengeUserRepository;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeUserService {

    private final ChallengeUserRepository repository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

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

    public ChallengeUser updateChallengeUserStatus(UUID id, ChallengeUserStatus newStatus) {
        ChallengeUser link = getChallengeUserById(id);
        link.setStatus(newStatus);
        if (newStatus == ChallengeUserStatus.ACCEPTED && link.getDateAccepted() == null) {
            link.setDateAccepted(LocalDate.now());
        }
        if (newStatus == ChallengeUserStatus.COMPLETED) {
            if (link.getDateAccepted() == null) {
                link.setDateAccepted(LocalDate.now());
            }
            link.setDateCompleted(LocalDate.now());
        }
        return repository.save(link);
    }

    public void deleteChallengeUser(UUID id) {
        ChallengeUser link = getChallengeUserById(id);
        repository.delete(link);
    }
}
