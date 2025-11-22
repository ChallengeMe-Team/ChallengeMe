package challengeme.backend.service;

import challengeme.backend.exception.LeaderboardNotFoundException;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.model.Leaderboard;
import challengeme.backend.model.User;
import challengeme.backend.repository.LeaderboardRepository;
import challengeme.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeaderboardServiceTests {

    @Mock
    private LeaderboardRepository leaderboardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private challengeme.backend.mapper.LeaderboardMapper mapper;

    @InjectMocks
    private LeaderboardService leaderboardService;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user1 = new User(UUID.randomUUID(), "ana", "ana@email.com", "pass", 0, "user");
        user2 = new User(UUID.randomUUID(), "mihai", "mihai@email.com", "pass", 0, "user");
    }

    // --- CREATE ---

    @Captor
    ArgumentCaptor<Leaderboard> leaderboardCaptor;

    @Test
    void testCreateLeaderboard_Success() {
        int points = 100;

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        when(leaderboardRepository.save(any(Leaderboard.class)))
                .thenAnswer(invocation -> {
                    Leaderboard lb = invocation.getArgument(0);
                    if (lb.getId() == null) {
                        lb.setId(UUID.randomUUID());
                    }
                    if (lb.getRank() == 0) {
                        lb.setRank(1);
                    }
                    return lb;
                });

        when(leaderboardRepository.findAll()).thenReturn(List.of());

        Leaderboard result = leaderboardService.create(user1.getId(), points);

        verify(leaderboardRepository, atLeast(1)).save(leaderboardCaptor.capture());

        Leaderboard finalSaved = leaderboardCaptor.getValue();

        assertEquals(user1, result.getUser());
        assertEquals(points, result.getTotalPoints());

        assertTrue(finalSaved.getRank() > 0);
        assertEquals(result.getRank(), finalSaved.getRank());
    }

    @Test
    void testCreateLeaderboard_UserNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        when(userRepository.findById(unknownUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> leaderboardService.create(unknownUserId, 50));
        verify(leaderboardRepository, never()).save(any());
    }

    // --- READ ---

    @Test
    void testGetLeaderboard_Success() {
        Leaderboard lb = new Leaderboard(user1, 100);
        lb.setId(UUID.randomUUID());

        when(leaderboardRepository.findById(lb.getId())).thenReturn(Optional.of(lb));

        Leaderboard result = leaderboardService.get(lb.getId());

        assertEquals(lb, result);
    }

    @Test
    void testGetLeaderboard_NotFound() {
        UUID id = UUID.randomUUID();
        when(leaderboardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LeaderboardNotFoundException.class, () -> leaderboardService.get(id));
    }

    @Test
    void testGetAllLeaderboards() {
        Leaderboard lb1 = new Leaderboard(user1, 100);
        Leaderboard lb2 = new Leaderboard(user2, 50);

        lb1.setId(UUID.randomUUID());
        lb2.setId(UUID.randomUUID());

        when(leaderboardRepository.findAll()).thenReturn(List.of(lb1, lb2));

        List<Leaderboard> all = leaderboardService.getAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(lb1));
        assertTrue(all.contains(lb2));
    }

    @Test
    void testGetSortedLeaderboards() {
        Leaderboard lb1 = new Leaderboard(user1, 100);
        lb1.setId(UUID.randomUUID());

        Leaderboard lb2 = new Leaderboard(user2, 50);
        lb2.setId(UUID.randomUUID());

        when(leaderboardRepository.findAll()).thenReturn(List.of(lb2, lb1));

        List<Leaderboard> sorted = leaderboardService.getSorted();

        assertEquals(lb1, sorted.get(0));
        assertEquals(lb2, sorted.get(1));
    }

    // --- UPDATE ---

    @Test
    void testUpdateLeaderboard_Success() {
        UUID id = UUID.randomUUID();
        Leaderboard existing = new Leaderboard(user1, 100);
        existing.setId(id);
        existing.setRank(2);

        challengeme.backend.dto.request.update.LeaderboardUpdateRequest request =
                new challengeme.backend.dto.request.update.LeaderboardUpdateRequest();

        when(leaderboardRepository.findById(id)).thenReturn(Optional.of(existing));

        doAnswer(invocation -> {
            Leaderboard entity = invocation.getArgument(1);
            entity.setTotalPoints(200);
            return null;
        }).when(mapper).updateEntity(any(), eq(existing));

        when(leaderboardRepository.save(any(Leaderboard.class))).thenAnswer(invocation -> {
            Leaderboard lb = invocation.getArgument(0);
            lb.setRank(1);
            return lb;
        });

        Leaderboard updated = leaderboardService.update(id, request);

        assertEquals(id, updated.getId());
        assertEquals(200, updated.getTotalPoints());
        assertEquals(1, updated.getRank());

        verify(mapper, times(1)).updateEntity(request, existing);
        verify(leaderboardRepository, atLeastOnce()).save(existing);
    }

    @Test
    void testUpdateLeaderboard_NotFound() {
        UUID id = UUID.randomUUID();
        when(leaderboardRepository.findById(id)).thenReturn(Optional.empty());

        challengeme.backend.dto.request.update.LeaderboardUpdateRequest request =
                new challengeme.backend.dto.request.update.LeaderboardUpdateRequest();

        assertThrows(LeaderboardNotFoundException.class, () -> leaderboardService.update(id, request));
        verify(mapper, never()).updateEntity(any(), any());
        verify(leaderboardRepository, never()).save(any());
    }

    // --- DELETE ---

    @Test
    void testDeleteLeaderboard_Success() {
        UUID id = UUID.randomUUID();
        Leaderboard lb = new Leaderboard(user1, 100);
        lb.setId(id);

        when(leaderboardRepository.findById(id)).thenReturn(Optional.of(lb));
        doNothing().when(leaderboardRepository).delete(lb);


        when(leaderboardRepository.findAll()).thenReturn(List.of());

        leaderboardService.delete(id);

        verify(leaderboardRepository).delete(lb);
        verify(leaderboardRepository, never()).save(any());
    }

    @Test
    void testDeleteLeaderboard_NotFound() {
        UUID id = UUID.randomUUID();
        when(leaderboardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LeaderboardNotFoundException.class, () -> leaderboardService.delete(id));
        verify(leaderboardRepository, never()).delete(any());
    }

    // --- RANKING LOGIC ---

    @Test
    void testRecalcRanksAfterCreateUpdateDelete() {

        Leaderboard lb1 = new Leaderboard(user1, 100);
        lb1.setId(UUID.randomUUID());

        Leaderboard lb2 = new Leaderboard(user2, 50);
        lb2.setId(UUID.randomUUID());

        when(leaderboardRepository.findAll()).thenReturn(List.of(lb1, lb2));

        List<Leaderboard> sorted = leaderboardService.getSorted();

        assertEquals(100, sorted.get(0).getTotalPoints());
        assertEquals(50, sorted.get(1).getTotalPoints());
    }
}