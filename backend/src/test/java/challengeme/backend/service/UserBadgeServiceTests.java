package challengeme.backend.service;

import challengeme.backend.dto.request.create.UserBadgeCreateRequest;
import challengeme.backend.dto.request.update.UserBadgeUpdateRequest;
import challengeme.backend.exception.UserBadgeNotFoundException;
import challengeme.backend.model.Badge;
import challengeme.backend.model.User;
import challengeme.backend.model.UserBadge;
import challengeme.backend.repository.BadgeRepository;
import challengeme.backend.repository.UserBadgeRepository;
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

class UserBadgeServiceTests {

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @InjectMocks
    private UserBadgeService userBadgeService;

    private UUID userId;
    private UUID badgeId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        badgeId = UUID.randomUUID();
    }

    // --- CREATE ---

    @Test
    void testCreateUserBadge_Success() {
        User user = new User(userId, "Ana", "ana@email.com", "secret", 10);
        Badge badge = new Badge(badgeId, "Gold", "Top badge", "Complete 10 challenges");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(badgeRepository.findById(badgeId)).thenReturn(Optional.of(badge));

        UserBadge saved = new UserBadge(UUID.randomUUID(), user, badge, LocalDate.now());
        when(userBadgeRepository.save(any(UserBadge.class))).thenReturn(saved);

        UserBadge result = userBadgeService.createUserBadge(userId, badgeId);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(badge, result.getBadge());
        assertNotNull(result.getDateAwarded());

        verify(userRepository).findById(userId);
        verify(badgeRepository).findById(badgeId);
        verify(userBadgeRepository).save(any(UserBadge.class));
    }

    @Test
    void testCreateUserBadge_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userBadgeService.createUserBadge(userId, badgeId));

        assertTrue(ex.getMessage().contains("User not found"));
        verify(userRepository).findById(userId);
        verifyNoInteractions(badgeRepository);
        verifyNoInteractions(userBadgeRepository);
    }

    @Test
    void testCreateUserBadge_BadgeNotFound() {
        User user = new User(userId, "Ana", "ana@email.com", "secret", 10);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(badgeRepository.findById(badgeId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userBadgeService.createUserBadge(userId, badgeId));

        assertTrue(ex.getMessage().contains("Badge not found"));
        verify(userRepository).findById(userId);
        verify(badgeRepository).findById(badgeId);
        verifyNoInteractions(userBadgeRepository);
    }

    // --- READ ---

    @Test
    void testFindUserBadge_Success() {
        UUID id = UUID.randomUUID();
        UserBadge ub = new UserBadge(id, new User(), new Badge(), LocalDate.now());

        when(userBadgeRepository.findById(id)).thenReturn(Optional.of(ub));

        UserBadge result = userBadgeService.findUserBadge(id);

        assertEquals(ub, result);
        verify(userBadgeRepository).findById(id);
    }

    @Test
    void testFindUserBadge_NotFound() {
        UUID id = UUID.randomUUID();
        when(userBadgeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserBadgeNotFoundException.class, () -> userBadgeService.findUserBadge(id));
        verify(userBadgeRepository).findById(id);
    }

    @Test
    void testFindAll() {
        UserBadge ub1 = new UserBadge();
        UserBadge ub2 = new UserBadge();

        when(userBadgeRepository.findAll()).thenReturn(List.of(ub1, ub2));

        List<UserBadge> all = userBadgeService.findAll();

        assertEquals(2, all.size());
        assertTrue(all.contains(ub1));
        assertTrue(all.contains(ub2));
        verify(userBadgeRepository).findAll();
    }

    // --- UPDATE ---

    @Test
    void testUpdateUserBadge_Success() {
        UUID id = UUID.randomUUID();
        LocalDate newDate = LocalDate.now().minusDays(1);
        UserBadge existing = new UserBadge(id, new User(), new Badge(), LocalDate.now());
        UserBadgeUpdateRequest request = new UserBadgeUpdateRequest();
        request.setDateAwarded(newDate);

        when(userBadgeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userBadgeRepository.save(existing)).thenReturn(existing);

        UserBadge updated = userBadgeService.updateUserBadge(id, request);

        assertEquals(newDate, updated.getDateAwarded());
        verify(userBadgeRepository).save(existing);
    }

    @Test
    void testUpdateUserBadge_NotFound() {
        UUID id = UUID.randomUUID();
        UserBadgeUpdateRequest request = new UserBadgeUpdateRequest();
        when(userBadgeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserBadgeNotFoundException.class, () -> userBadgeService.updateUserBadge(id, request));
        verify(userBadgeRepository, never()).save(any());
    }

    // --- DELETE ---

    @Test
    void testDeleteUserBadge_Success() {
        UUID id = UUID.randomUUID();
        UserBadge ub = new UserBadge(id, new User(), new Badge(), LocalDate.now());
        when(userBadgeRepository.findById(id)).thenReturn(Optional.of(ub));
        doNothing().when(userBadgeRepository).delete(ub);

        assertDoesNotThrow(() -> userBadgeService.deleteUserBadge(id));

        verify(userBadgeRepository).delete(ub);
    }

    @Test
    void testDeleteUserBadge_NotFound() {
        UUID id = UUID.randomUUID();
        when(userBadgeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserBadgeNotFoundException.class, () -> userBadgeService.deleteUserBadge(id));
        verify(userBadgeRepository, never()).delete(any());
    }
}
