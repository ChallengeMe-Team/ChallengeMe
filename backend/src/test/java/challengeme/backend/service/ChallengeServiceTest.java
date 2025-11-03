package challengeme.backend.service;

import challengeme.backend.domain.Challenge;
import challengeme.backend.repo.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    private ChallengeService challengeService;

    @BeforeEach
    void setUp() {
        challengeService = new ChallengeService(challengeRepository);
    }

    @Test
    void shouldAddValidChallenge() {
        Challenge challenge = new Challenge(
                "Valid Title", "Description", "Category",
                Challenge.Difficulty.EASY, 100, "user123"
        );

        when(challengeRepository.save(any(Challenge.class))).thenReturn(challenge);

        Challenge result = challengeService.addChallenge(challenge);

        assertNotNull(result);
        verify(challengeRepository).save(challenge);
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        Challenge challenge = new Challenge();
        challenge.setTitle("");
        challenge.setCategory("Category");
        challenge.setDifficulty(Challenge.Difficulty.EASY);
        challenge.setPoints(100);
        challenge.setCreatedBy("user123");

        assertThrows(IllegalArgumentException.class, () -> {
            challengeService.addChallenge(challenge);
        });
    }
}