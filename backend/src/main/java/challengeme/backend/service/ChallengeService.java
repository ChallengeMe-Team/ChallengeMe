package challengeme.backend.service;

import challengeme.backend.dto.request.update.ChallengeUpdateRequest;
import challengeme.backend.mapper.ChallengeMapper;
import challengeme.backend.model.Challenge;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository repository;
    private final ChallengeMapper mapper;

    public List<Challenge> getAllChallenges() {
        return repository.findAll();
    }

    public Challenge getChallengeById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ChallengeNotFoundException(id));
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


    public void deleteChallenge(UUID id) {
        if (!repository.existsById(id)) {
            throw new ChallengeNotFoundException(id);
        }
        repository.deleteById(id);
    }

}
