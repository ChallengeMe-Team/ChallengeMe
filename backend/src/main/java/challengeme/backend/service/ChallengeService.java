package challengeme.backend.service;

import challengeme.backend.model.Challenge;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.repository.inMemory.InMemoryChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class ChallengeService {
    private final InMemoryChallengeRepository inMemoryChallengeRepository;

    @Autowired
    public ChallengeService(InMemoryChallengeRepository inMemoryChallengeRepository) {
        this.inMemoryChallengeRepository = inMemoryChallengeRepository;
    }

    public List<Challenge> getAllChallenges() {
        return inMemoryChallengeRepository.findAll();
    }

    public Challenge getChallengeById(UUID id) {
        return inMemoryChallengeRepository.findById(id)
                .orElseThrow(() -> new ChallengeNotFoundException(id));
    }
    
    public Challenge addChallenge(Challenge challenge) {
        validateChallenge(challenge);
        return inMemoryChallengeRepository.save(challenge);
    }

    public Challenge updateChallenge(UUID id, Challenge challenge) {
        if(!inMemoryChallengeRepository.existsById(id)) {
            throw new ChallengeNotFoundException(id);
        }
        challenge.setId(id);
        validateChallenge(challenge);
        return inMemoryChallengeRepository.save(challenge);
    }

    public void deleteChallenge(UUID id) {
        if(!inMemoryChallengeRepository.existsById(id)) {
            throw new ChallengeNotFoundException(id);
        }
        inMemoryChallengeRepository.deleteById(id);
    }

    private void validateChallenge(Challenge challenge) {
        if (challenge.getTitle() == null || challenge.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (challenge.getCategory() == null || challenge.getCategory().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        if (challenge.getDifficulty() == null) {
            throw new IllegalArgumentException("Difficulty must be specified");
        }
        if (challenge.getPoints() <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }
        if (challenge.getCreatedBy() == null || challenge.getCreatedBy().trim().isEmpty()) {
            throw new IllegalArgumentException("Created by cannot be empty");
        }
    }
}
