package challengeme.backend.service;

import challengeme.backend.model.Challenge;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.repository.inMemory.InMemoryChallengeRepository;
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
    private InMemoryChallengeRepository inMemoryChallengeRepository;

    private ChallengeService challengeService;

    @BeforeEach
    void setUp() {
        challengeService = new ChallengeService(inMemoryChallengeRepository);
    }

    @Test
    void shouldGetAllChallenges() {
        when(inMemoryChallengeRepository.findAll()).thenReturn(List.of(new Challenge()));
        assertEquals(1, challengeService.getAllChallenges().size());
    }

    @Test
    void shouldGetChallengeById() {
        UUID id = UUID.randomUUID();
        Challenge c = new Challenge("T", "D", "C", Challenge.Difficulty.EASY, 10, "U");
        when(inMemoryChallengeRepository.findById(id)).thenReturn(Optional.of(c));

        Challenge result = challengeService.getChallengeById(id);
        assertEquals("T", result.getTitle());
    }

    @Test
    void shouldThrowWhenChallengeNotFound() {
        UUID id = UUID.randomUUID();
        when(inMemoryChallengeRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ChallengeNotFoundException.class, () -> challengeService.getChallengeById(id));
    }

    @Test
    void shouldAddValidChallenge() {
        Challenge challenge = new Challenge("T", "D", "C", Challenge.Difficulty.EASY, 10, "U");
        when(inMemoryChallengeRepository.save(any())).thenReturn(challenge);
        Challenge result = challengeService.addChallenge(challenge);
        assertEquals(challenge, result);
    }

    @Test
    void shouldThrowExceptionWhenTitleEmpty() {
        Challenge challenge = new Challenge("", "D", "C", Challenge.Difficulty.EASY, 10, "U");
        assertThrows(IllegalArgumentException.class, () -> challengeService.addChallenge(challenge));
    }

    @Test
    void shouldUpdateExistingChallenge() {
        UUID id = UUID.randomUUID();
        Challenge challenge = new Challenge("T", "D", "C", Challenge.Difficulty.EASY, 10, "U");
        when(inMemoryChallengeRepository.existsById(id)).thenReturn(true);
        when(inMemoryChallengeRepository.save(any())).thenReturn(challenge);

        Challenge updated = challengeService.updateChallenge(id, challenge);
        assertEquals("T", updated.getTitle());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingChallenge() {
        UUID id = UUID.randomUUID();
        Challenge challenge = new Challenge("T", "D", "C", Challenge.Difficulty.EASY, 10, "U");
        when(inMemoryChallengeRepository.existsById(id)).thenReturn(false);

        assertThrows(ChallengeNotFoundException.class, () -> challengeService.updateChallenge(id, challenge));
    }

    @Test
    void shouldDeleteExistingChallenge() {
        UUID id = UUID.randomUUID();
        when(inMemoryChallengeRepository.existsById(id)).thenReturn(true);
        challengeService.deleteChallenge(id);
        verify(inMemoryChallengeRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingChallenge() {
        UUID id = UUID.randomUUID();
        when(inMemoryChallengeRepository.existsById(id)).thenReturn(false);
        assertThrows(ChallengeNotFoundException.class, () -> challengeService.deleteChallenge(id));
    }
}
