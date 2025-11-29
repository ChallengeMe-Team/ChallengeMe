package challengeme.backend.service;

import challengeme.backend.dto.request.update.ChallengeUpdateRequest;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.mapper.ChallengeMapper;
import challengeme.backend.model.Challenge;
import challengeme.backend.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChallengeServiceTests {

    @Mock
    private ChallengeRepository repository;

    @Mock
    private ChallengeMapper mapper;

    private ChallengeService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new ChallengeService(repository, mapper);
    }

    @Test
    void testGetAllChallenges() {
        Challenge c1 = new Challenge(null, "Title1", "Desc1", "Cat1", Challenge.Difficulty.EASY, 10, "User1");
        Challenge c2 = new Challenge(null, "Title2", "Desc2", "Cat2", Challenge.Difficulty.MEDIUM, 20, "User2");

        when(repository.findAll()).thenReturn(List.of(c1, c2));

        List<Challenge> result = service.getAllChallenges();
        assertEquals(2, result.size());
        assertTrue(result.contains(c1));
        assertTrue(result.contains(c2));

        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetChallengeByIdExists() {
        UUID id = UUID.randomUUID();
        Challenge c = new Challenge(null, "Title", "Desc", "Cat", Challenge.Difficulty.HARD, 30, "User");

        when(repository.findById(id)).thenReturn(Optional.of(c));

        Challenge result = service.getChallengeById(id);
        assertEquals("Title", result.getTitle());

        verify(repository, times(1)).findById(id);
    }

    @Test
    void testGetChallengeByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ChallengeNotFoundException.class, () -> service.getChallengeById(id));

        verify(repository, times(1)).findById(id);
    }

    @Test
    void testAddChallenge() {
        Challenge c = new Challenge(null, "Title", "Desc", "Cat", Challenge.Difficulty.EASY, 10, "User");

        when(repository.save(c)).thenReturn(c);

        Challenge result = service.addChallenge(c);
        assertEquals(c, result);

        verify(repository, times(1)).save(c);
    }

    @Test
    void testUpdateChallengeExists() {
        UUID id = UUID.randomUUID();
        Challenge existing = new Challenge(null, "OldTitle", "OldDesc", "OldCat", Challenge.Difficulty.EASY, 10, "User");
        ChallengeUpdateRequest request = new ChallengeUpdateRequest("NewTitle", "NewDesc", "NewCat", Challenge.Difficulty.MEDIUM, 20, "User");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        // Mapper-ul este apelat, dar nu accesăm atributele DTO-ului
        doNothing().when(mapper).updateEntity(request, existing);

        Challenge updated = service.updateChallenge(id, request);

        assertNotNull(updated); // verificăm că returnează entity-ul
        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).updateEntity(request, existing);
        verify(repository, times(1)).save(existing);
    }

    @Test
    void testUpdateChallengeNotFound() {
        UUID id = UUID.randomUUID();
        ChallengeUpdateRequest request = new ChallengeUpdateRequest("NewTitle", "NewDesc", "NewCat", Challenge.Difficulty.MEDIUM, 20, "User");

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ChallengeNotFoundException.class, () -> service.updateChallenge(id, request));

        verify(repository, times(1)).findById(id);
        verify(mapper, never()).updateEntity(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void testDeleteChallengeExists() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deleteChallenge(id);

        verify(repository, times(1)).existsById(id);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteChallengeNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(ChallengeNotFoundException.class, () -> service.deleteChallenge(id));

        verify(repository, times(1)).existsById(id);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void updateChallenge_shouldReturn403_whenUserNotOwner() {
        UUID id = UUID.randomUUID();

        Challenge challenge = new Challenge();
        challenge.setId(id);
        challenge.setCreatedBy("otherUser");

        when(repository.findById(id)).thenReturn(Optional.of(challenge));

        Authentication auth =
                new UsernamePasswordAuthenticationToken("theUser", null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        ChallengeUpdateRequest req = new ChallengeUpdateRequest("Updated Title",
                "Updated Description",
                "Fitness",
                Challenge.Difficulty.EASY,
                50,
                "doesNotMatter");

        assertThrows(ResponseStatusException.class, () -> {
            service.updateChallenge(id, req);
        });
    }
}