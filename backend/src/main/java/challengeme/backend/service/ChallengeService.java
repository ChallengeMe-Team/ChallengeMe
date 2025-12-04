package challengeme.backend.service;

import challengeme.backend.dto.request.update.ChallengeUpdateRequest;
import challengeme.backend.mapper.ChallengeMapper;
import challengeme.backend.model.Challenge;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.repository.ChallengeRepository;
import challengeme.backend.repository.ChallengeUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository repository;
    private final ChallengeUserRepository challengeUserRepository;
    private final ChallengeMapper mapper;

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

}