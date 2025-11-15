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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChallengeUserServiceTests {

    @Mock
    private ChallengeUserRepository challengeUserRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeUserService challengeUserService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --- CREATE ---

    @Test
    void testCreateChallengeUser_Success() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        // FIX: User constructor now takes UUID ID
        User user = new User(userId, "TestUser", "test@email.com", "secure_pass", 0);
        // FIX: Assuming Challenge constructor now takes UUID ID
        Challenge challenge = new Challenge(challengeId, "Title", "Desc", "Cat", Challenge.Difficulty.EASY, 10, "Creator");

        ChallengeUserCreateRequest request = mock(ChallengeUserCreateRequest.class);
        when(request.getUserId()).thenReturn(userId);
        when(request.getChallengeId()).thenReturn(challengeId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));

        when(challengeUserRepository.save(any())).thenAnswer(invocation -> {
            ChallengeUser cu = invocation.getArgument(0);
            cu.setId(UUID.randomUUID());
            return cu;
        });

        ChallengeUser result = challengeUserService.createChallengeUser(request);

        assertNotNull(result.getId());
        assertEquals(user, result.getUser());
        assertEquals(challenge, result.getChallenge());
        assertEquals(ChallengeUserStatus.PENDING, result.getStatus());
        assertNull(result.getDateAccepted());
        assertNull(result.getDateCompleted());

        verify(challengeUserRepository).save(any());
    }

    @Test
    void testCreateChallengeUser_UserNotFound() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        ChallengeUserCreateRequest request = mock(ChallengeUserCreateRequest.class);
        when(request.getUserId()).thenReturn(userId);
        when(request.getChallengeId()).thenReturn(challengeId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> challengeUserService.createChallengeUser(request));
        verify(challengeRepository, never()).findById(any());
        verify(challengeUserRepository, never()).save(any());
    }

    @Test
    void testCreateChallengeUser_ChallengeNotFound() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        User user = new User(userId, "TestUser", "test@email.com", "secure_pass", 0);

        ChallengeUserCreateRequest request = mock(ChallengeUserCreateRequest.class);
        when(request.getUserId()).thenReturn(userId);
        when(request.getChallengeId()).thenReturn(challengeId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.empty());

        assertThrows(ChallengeNotFoundException.class, () -> challengeUserService.createChallengeUser(request));
        verify(challengeUserRepository, never()).save(any());
    }

    // --- READ ---

    @Test
    void testGetChallengeUserById_Success() {
        UUID id = UUID.randomUUID();
        User mockUser = new User(UUID.randomUUID(), "U", "u@e.com", "p", 0);
        Challenge mockChallenge = new Challenge(UUID.randomUUID(), "C", "D", "Cat", Challenge.Difficulty.EASY, 1, "Cr");

        ChallengeUser cu = new ChallengeUser(id, mockUser, mockChallenge, ChallengeUserStatus.ACCEPTED, LocalDate.now(), null);
        when(challengeUserRepository.findById(id)).thenReturn(Optional.of(cu));

        ChallengeUser result = challengeUserService.getChallengeUserById(id);
        assertEquals(cu, result);
        verify(challengeUserRepository).findById(id);
    }

    @Test
    void testGetChallengeUserById_NotFound() {
        UUID id = UUID.randomUUID();
        when(challengeUserRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ChallengeUserNotFoundException.class, () -> challengeUserService.getChallengeUserById(id));
    }

    @Test
    void testGetAllChallengeUsers() {
        ChallengeUser cu1 = new ChallengeUser();
        ChallengeUser cu2 = new ChallengeUser();
        when(challengeUserRepository.findAll()).thenReturn(List.of(cu1, cu2));

        List<ChallengeUser> results = challengeUserService.getAllChallengeUsers();
        assertEquals(2, results.size());
        verify(challengeUserRepository).findAll();
    }

    @Test
    void testGetChallengeUsersByUserId() {
        UUID inputUserId = UUID.randomUUID();

        User mockUser = new User(inputUserId, "Name", "email", "pass", 0);
        ChallengeUser cu1 = new ChallengeUser(UUID.randomUUID(), mockUser, new Challenge(), ChallengeUserStatus.PENDING, null, null);

        when(challengeUserRepository.findByUserId(inputUserId)).thenReturn(List.of(cu1));

        List<ChallengeUser> results = challengeUserService.getChallengeUsersByUserId(inputUserId);
        assertEquals(1, results.size());

        assertEquals("Name", results.get(0).getUser().getUsername());
        verify(challengeUserRepository).findByUserId(inputUserId);
    }

    // --- UPDATE ---

    @Test
    void testUpdateChallengeUserStatus_ToAccepted() {
        UUID id = UUID.randomUUID();
        User mockUser = new User(UUID.randomUUID(), "U", "u@e.com", "p", 0);
        Challenge mockChallenge = new Challenge(UUID.randomUUID(), "C", "D", "Cat", Challenge.Difficulty.EASY, 1, "Cr");

        ChallengeUser cu = new ChallengeUser(id, mockUser, mockChallenge, ChallengeUserStatus.PENDING, null, null);

        when(challengeUserRepository.findById(id)).thenReturn(Optional.of(cu));
        when(challengeUserRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ChallengeUser result = challengeUserService.updateChallengeUserStatus(id, ChallengeUserStatus.ACCEPTED);

        assertEquals(ChallengeUserStatus.ACCEPTED, result.getStatus());
        assertNotNull(result.getDateAccepted());
        assertNull(result.getDateCompleted());
        verify(challengeUserRepository).save(cu);
    }

    @Test
    void testUpdateChallengeUserStatus_ToCompleted() {
        UUID id = UUID.randomUUID();
        User mockUser = new User(UUID.randomUUID(), "U", "u@e.com", "p", 0);
        Challenge mockChallenge = new Challenge(UUID.randomUUID(), "C", "D", "Cat", Challenge.Difficulty.EASY, 1, "Cr");

        ChallengeUser cu = new ChallengeUser(id, mockUser, mockChallenge, ChallengeUserStatus.ACCEPTED, LocalDate.now().minusDays(1), null);

        when(challengeUserRepository.findById(id)).thenReturn(Optional.of(cu));
        when(challengeUserRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ChallengeUser result = challengeUserService.updateChallengeUserStatus(id, ChallengeUserStatus.COMPLETED);

        assertEquals(ChallengeUserStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getDateAccepted());
        assertEquals(LocalDate.now(), result.getDateCompleted());
        verify(challengeUserRepository).save(cu);
    }

    @Test
    void testUpdateChallengeUserStatus_NotFound() {
        UUID id = UUID.randomUUID();
        when(challengeUserRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ChallengeUserNotFoundException.class, () ->
                challengeUserService.updateChallengeUserStatus(id, ChallengeUserStatus.ACCEPTED));
        verify(challengeUserRepository, never()).save(any());
    }

    // --- DELETE ---

    @Test
    void testDeleteChallengeUser_Success() {
        UUID id = UUID.randomUUID();
        ChallengeUser cu = new ChallengeUser();
        when(challengeUserRepository.findById(id)).thenReturn(Optional.of(cu));

        challengeUserService.deleteChallengeUser(id);

        verify(challengeUserRepository).delete(cu);
    }

    @Test
    void testDeleteChallengeUser_NotFound() {
        UUID id = UUID.randomUUID();
        when(challengeUserRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ChallengeUserNotFoundException.class, () -> challengeUserService.deleteChallengeUser(id));
        verify(challengeUserRepository, never()).delete(any());
    }
}