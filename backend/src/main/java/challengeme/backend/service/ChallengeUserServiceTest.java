package challengeme.backend.service;

import challengeme.backend.exception.ResourceNotFoundException;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.ChallengeUserStatus;
import challengeme.backend.model.CreateChallengeUserRequest;
import challengeme.backend.repository.ChallengeUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Teste unitare pentru ChallengeUserService.
 */
@ExtendWith(MockitoExtension.class)
class ChallengeUserServiceTest {

    @Mock
    private ChallengeUserRepository challengeUserRepository;

    @Mock private UserService userService;
    //@Mock private ChallengeService challengeService; // De-comentează când e adaugat

    @InjectMocks
    private ChallengeUserService challengeUserService;

    // --- CREATE ---

    @Test
    void testCreateChallengeUser_Success() {

        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        CreateChallengeUserRequest request = new CreateChallengeUserRequest();
        request.setUserId(userId);
        request.setChallengeId(challengeId);

        when(challengeUserRepository.save(any(ChallengeUser.class))).thenAnswer(invocation -> {
            ChallengeUser cu = invocation.getArgument(0);
            cu.setId(UUID.randomUUID());
            return cu;
        });

        ChallengeUser result = challengeUserService.createChallengeUser(request);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(challengeId, result.getChallengeId());
        assertEquals(ChallengeUserStatus.PENDING, result.getStatus());
        assertNull(result.getDateAccepted());
        assertNull(result.getDateCompleted());

        verify(challengeUserRepository).save(any(ChallengeUser.class));
    }

    @Test
    void testCreateChallengeUser_NullUserId_ThrowsException() {
        // Given
        CreateChallengeUserRequest request = new CreateChallengeUserRequest();
        request.setUserId(null);
        request.setChallengeId(UUID.randomUUID());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            challengeUserService.createChallengeUser(request);
        });

        assertEquals("User ID and Challenge ID cannot be null", exception.getMessage());
        verify(challengeUserRepository, never()).save(any());
    }

    // --- READ ---

    @Test
    void testGetChallengeUserById_Success() {
        UUID id = UUID.randomUUID();
        ChallengeUser mockChallengeUser = new ChallengeUser(id, UUID.randomUUID(), UUID.randomUUID(), ChallengeUserStatus.ACCEPTED, LocalDate.now(), null);
        when(challengeUserRepository.findById(id)).thenReturn(Optional.of(mockChallengeUser));

        ChallengeUser result = challengeUserService.getChallengeUserById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(challengeUserRepository).findById(id);
    }

    @Test
    void testGetChallengeUserById_NotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(challengeUserRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            challengeUserService.getChallengeUserById(id);
        });

        assertEquals("ChallengeUser link not found with id: " + id, exception.getMessage());
    }

    @Test
    void testGetAllChallengeUsers() {
        ChallengeUser cu1 = new ChallengeUser(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), ChallengeUserStatus.PENDING, null, null);
        ChallengeUser cu2 = new ChallengeUser(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), ChallengeUserStatus.COMPLETED, LocalDate.now(), LocalDate.now());
        when(challengeUserRepository.findAll()).thenReturn(List.of(cu1, cu2));

        List<ChallengeUser> results = challengeUserService.getAllChallengeUsers();

        assertNotNull(results);
        assertEquals(2, results.size());
        verify(challengeUserRepository).findAll();
    }

    @Test
    void testGetChallengeUsersByUserId() {
        UUID userId = UUID.randomUUID();
        ChallengeUser cu1 = new ChallengeUser(UUID.randomUUID(), userId, UUID.randomUUID(), ChallengeUserStatus.PENDING, null, null);
        when(challengeUserRepository.findByUserId(userId)).thenReturn(List.of(cu1));

        List<ChallengeUser> results = challengeUserService.getChallengeUsersByUserId(userId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(userId, results.get(0).getUserId());
        verify(challengeUserRepository).findByUserId(userId);
    }

    // --- UPDATE ---

    @Test
    void testUpdateChallengeUserStatus_ToCompleted_Success() {
        UUID id = UUID.randomUUID();
        ChallengeUser existingChallengeUser = new ChallengeUser(id, UUID.randomUUID(), UUID.randomUUID(), ChallengeUserStatus.ACCEPTED, LocalDate.now().minusDays(1), null);

        when(challengeUserRepository.findById(id)).thenReturn(Optional.of(existingChallengeUser));
        when(challengeUserRepository.save(any(ChallengeUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChallengeUser result = challengeUserService.updateChallengeUserStatus(id, ChallengeUserStatus.COMPLETED);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(ChallengeUserStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getDateAccepted());
        assertEquals(LocalDate.now(), result.getDateCompleted());

        verify(challengeUserRepository).findById(id);
        verify(challengeUserRepository).save(existingChallengeUser);
    }

    @Test
    void testUpdateChallengeUserStatus_ToAccepted_Success() {
        UUID id = UUID.randomUUID();
        ChallengeUser existingChallengeUser = new ChallengeUser(id, UUID.randomUUID(), UUID.randomUUID(), ChallengeUserStatus.PENDING, null, null);

        when(challengeUserRepository.findById(id)).thenReturn(Optional.of(existingChallengeUser));
        when(challengeUserRepository.save(any(ChallengeUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChallengeUser result = challengeUserService.updateChallengeUserStatus(id, ChallengeUserStatus.ACCEPTED);

        assertNotNull(result);
        assertEquals(ChallengeUserStatus.ACCEPTED, result.getStatus());
        assertEquals(LocalDate.now(), result.getDateAccepted());
        assertNull(result.getDateCompleted());

        verify(challengeUserRepository).save(existingChallengeUser);
    }

    @Test
    void testUpdateChallengeUserStatus_NotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(challengeUserRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            challengeUserService.updateChallengeUserStatus(id, ChallengeUserStatus.COMPLETED);
        });

        assertEquals("ChallengeUser link not found with id: " + id, exception.getMessage());
        verify(challengeUserRepository, never()).save(any());
    }

    // --- DELETE ---

    @Test
    void testDeleteChallengeUser_Success() {
        UUID id = UUID.randomUUID();
        when(challengeUserRepository.findById(id)).thenReturn(Optional.of(new ChallengeUser()));
        doNothing().when(challengeUserRepository).deleteById(id);

        challengeUserService.deleteChallengeUser(id);

        verify(challengeUserRepository).findById(id);
        verify(challengeUserRepository).deleteById(id);
    }

    @Test
    void testDeleteChallengeUser_NotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(challengeUserRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            challengeUserService.deleteChallengeUser(id);
        });

        assertEquals("ChallengeUser link not found with id: " + id, exception.getMessage());
        verify(challengeUserRepository, never()).deleteById(id);
    }
}
