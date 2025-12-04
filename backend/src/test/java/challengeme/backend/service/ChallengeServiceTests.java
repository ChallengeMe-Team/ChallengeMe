package challengeme.backend.service;

import challengeme.backend.dto.request.update.ChallengeUpdateRequest;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.mapper.ChallengeMapper;
import challengeme.backend.model.Challenge;
import challengeme.backend.repository.ChallengeRepository;
import challengeme.backend.repository.ChallengeUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
    private ChallengeUserRepository challengeUserRepository; // 1. Dependență nouă adăugată

    @Mock
    private ChallengeMapper mapper;

    private ChallengeService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // 2. Constructor actualizat cu cele 3 argumente
        service = new ChallengeService(repository, challengeUserRepository, mapper);
    }

    @AfterEach
    void tearDown() {
        // Curățăm contextul de securitate după fiecare test ca să nu se influențeze între ele
        SecurityContextHolder.clearContext();
    }

    // --- Helper pentru a simula userul logat ---
    private void mockSecurityContext(String username) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAllChallenges() {
        Challenge c1 = new Challenge(null, "Title1", "Desc1", "Cat1", Challenge.Difficulty.EASY, 10, "User1");
        Challenge c2 = new Challenge(null, "Title2", "Desc2", "Cat2", Challenge.Difficulty.MEDIUM, 20, "User2");

        when(repository.findAll()).thenReturn(List.of(c1, c2));

        List<Challenge> result = service.getAllChallenges();
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetChallengeByIdExists() {
        UUID id = UUID.randomUUID();
        Challenge c = new Challenge(null, "Title", "Desc", "Cat", Challenge.Difficulty.HARD, 30, "User");

        when(repository.findById(id)).thenReturn(Optional.of(c));

        Challenge result = service.getChallengeById(id);
        assertEquals("Title", result.getTitle());
    }

    @Test
    void testGetChallengeByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ChallengeNotFoundException.class, () -> service.getChallengeById(id));
    }

    @Test
    void testAddChallenge() {
        Challenge c = new Challenge(null, "Title", "Desc", "Cat", Challenge.Difficulty.EASY, 10, "User");
        when(repository.save(c)).thenReturn(c);

        Challenge result = service.addChallenge(c);
        assertEquals(c, result);
    }

    @Test
    void testUpdateChallengeSuccess() {
        UUID id = UUID.randomUUID();
        String owner = "OwnerUser";

        // Setup challenge existent
        Challenge existing = new Challenge(id, "OldTitle", "OldDesc", "OldCat", Challenge.Difficulty.EASY, 10, owner);
        ChallengeUpdateRequest request = new ChallengeUpdateRequest("NewTitle", "NewDesc", "NewCat", Challenge.Difficulty.MEDIUM, 20, owner);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        // Simulăm că suntem logați ca proprietarul challenge-ului
        mockSecurityContext(owner);

        Challenge updated = service.updateChallenge(id, request);

        assertNotNull(updated);
        verify(mapper, times(1)).updateEntity(request, existing); // Verificăm că s-a făcut update la câmpuri
        verify(repository, times(1)).save(existing);
    }

    @Test
    void testUpdateChallengeForbidden() {
        UUID id = UUID.randomUUID();
        Challenge existing = new Challenge(id, "Title", "Desc", "Cat", Challenge.Difficulty.EASY, 10, "OriginalOwner");
        ChallengeUpdateRequest request = new ChallengeUpdateRequest("New", "New", "New", Challenge.Difficulty.EASY, 10, "Hacker");

        when(repository.findById(id)).thenReturn(Optional.of(existing));

        // Simulăm un alt user decât proprietarul
        mockSecurityContext("OtherUser");

        assertThrows(ResponseStatusException.class, () -> service.updateChallenge(id, request));

        verify(repository, never()).save(any()); // Ne asigurăm că NU s-a salvat nimic
    }

    @Test
    void testDeleteChallengeSuccess() {
        UUID id = UUID.randomUUID();
        String owner = "OwnerUser";
        Challenge existing = new Challenge(id, "Title", "Desc", "Cat", Challenge.Difficulty.EASY, 10, owner);

        // Serviciul folosește findById, nu existsById acum
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        // Simulăm userul corect
        mockSecurityContext(owner);

        service.deleteChallenge(id);

        // Verificăm ordinea operațiunilor
        verify(challengeUserRepository, times(1)).deleteAllByChallengeId(id); // 1. Șterge dependențele
        verify(repository, times(1)).delete(existing); // 2. Șterge challenge-ul
    }

    @Test
    void testDeleteChallengeForbidden() {
        UUID id = UUID.randomUUID();
        Challenge existing = new Challenge(id, "Title", "Desc", "Cat", Challenge.Difficulty.EASY, 10, "RealOwner");

        when(repository.findById(id)).thenReturn(Optional.of(existing));

        // Simulăm un intrus
        mockSecurityContext("Intruder");

        assertThrows(ResponseStatusException.class, () -> service.deleteChallenge(id));

        verify(repository, never()).delete(any());
        verify(challengeUserRepository, never()).deleteAllByChallengeId(any());
    }

    @Test
    void testDeleteChallengeNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ChallengeNotFoundException.class, () -> service.deleteChallenge(id));
    }
}