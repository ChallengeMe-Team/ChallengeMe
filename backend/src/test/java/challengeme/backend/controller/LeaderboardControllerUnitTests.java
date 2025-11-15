package challengeme.backend.controller;

import challengeme.backend.dto.LeaderboardDTO;
import challengeme.backend.dto.request.create.LeaderboardCreateRequest;
import challengeme.backend.dto.request.update.LeaderboardUpdateRequest;
import challengeme.backend.exception.GlobalExceptionHandler;
import challengeme.backend.exception.LeaderboardNotFoundException;
import challengeme.backend.mapper.LeaderboardMapper;
import challengeme.backend.model.Leaderboard;
import challengeme.backend.model.User;
import challengeme.backend.service.LeaderboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

//Unit tests cu MockMvc – testează controller-ul izolat, folosind mock pentru LeaderboardService.

@ExtendWith(MockitoExtension.class)
public class LeaderboardControllerUnitTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private LeaderboardService leaderboardService;

    @Mock
    private LeaderboardMapper leaderboardMapper;

    @InjectMocks
    private LeaderboardController leaderboardController;

    private UUID userId;
    private UUID leaderboardId;
    private User user;
    private Leaderboard leaderboard;
    private LeaderboardDTO leaderboardDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(leaderboardController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userId = UUID.randomUUID();
        leaderboardId = UUID.randomUUID();

        user = mock(User.class);

        leaderboard = new Leaderboard(leaderboardId, user, 100, 1);

        leaderboardDTO = new LeaderboardDTO();
        leaderboardDTO.setId(leaderboardId);
        leaderboardDTO.setUserId(userId);
        leaderboardDTO.setTotalPoints(100);
        leaderboardDTO.setRank(1);
    }

    @Test
    void testGetAllLeaderboardEntries() throws Exception {
        when(leaderboardService.getAll()).thenReturn(List.of(leaderboard));
        when(leaderboardMapper.toDTO(leaderboard)).thenReturn(leaderboardDTO);

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(leaderboardId.toString())));
    }

    @Test
    void testGetLeaderboardById() throws Exception {
        when(leaderboardService.get(leaderboardId)).thenReturn(leaderboard);
        when(leaderboardMapper.toDTO(leaderboard)).thenReturn(leaderboardDTO);

        mockMvc.perform(get("/api/leaderboard/{id}", leaderboardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(leaderboardId.toString())))
                .andExpect(jsonPath("$.totalPoints", is(100)));
    }

    @Test
    void testGetLeaderboardById_NotFound() throws Exception {
        when(leaderboardService.get(leaderboardId)).thenThrow(new LeaderboardNotFoundException(leaderboardId));

        mockMvc.perform(get("/api/leaderboard/{id}", leaderboardId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Leaderboard entry not found with id: " + leaderboardId));
    }

    @Test
    void testCreateLeaderboardEntry() throws Exception {
        final int newPoints = 120;

        LeaderboardCreateRequest req = new LeaderboardCreateRequest();
        req.setUserId(userId);
        req.setTotalPoints(newPoints);

        Leaderboard createdLeaderboard = new Leaderboard(UUID.randomUUID(), user, newPoints, 1);
        LeaderboardDTO createdDTO = new LeaderboardDTO();
        createdDTO.setId(createdLeaderboard.getId());
        createdDTO.setUserId(userId);
        createdDTO.setTotalPoints(newPoints);
        createdDTO.setRank(1);

        when(leaderboardService.create(userId, newPoints)).thenReturn(createdLeaderboard);
        when(leaderboardMapper.toDTO(createdLeaderboard)).thenReturn(createdDTO);

        mockMvc.perform(post("/api/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createdLeaderboard.getId().toString())))
                .andExpect(jsonPath("$.totalPoints", is(newPoints)));

        verify(leaderboardService, times(1)).create(userId, newPoints);
    }

    @Test
    void testUpdateLeaderboardEntry() throws Exception {
        final int updatedPoints = 200;

        LeaderboardUpdateRequest req = new LeaderboardUpdateRequest();
        req.setTotalPoints(updatedPoints);

        Leaderboard updatedLeaderboard = new Leaderboard(leaderboardId, user, updatedPoints, 1);
        LeaderboardDTO updatedDTO = new LeaderboardDTO();
        updatedDTO.setId(leaderboardId);
        updatedDTO.setUserId(userId);
        updatedDTO.setTotalPoints(updatedPoints);
        updatedDTO.setRank(1);

        when(leaderboardService.update(leaderboardId, req)).thenReturn(updatedLeaderboard);
        when(leaderboardMapper.toDTO(updatedLeaderboard)).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/leaderboard/{id}", leaderboardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints", is(updatedPoints)));

        verify(leaderboardService, times(1)).update(leaderboardId, req);
    }

    @Test
    void testUpdateLeaderboardEntry_NotFound() throws Exception {
        LeaderboardUpdateRequest req = new LeaderboardUpdateRequest();
        req.setTotalPoints(200);

        when(leaderboardService.update(leaderboardId, req))
                .thenThrow(new LeaderboardNotFoundException(leaderboardId));

        mockMvc.perform(put("/api/leaderboard/{id}", leaderboardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Leaderboard entry not found with id: " + leaderboardId));
    }

    @Test
    void testDeleteLeaderboardEntry() throws Exception {
        doNothing().when(leaderboardService).delete(leaderboardId);

        mockMvc.perform(delete("/api/leaderboard/{id}", leaderboardId))
                .andExpect(status().isNoContent());

        verify(leaderboardService, times(1)).delete(leaderboardId);
    }

    @Test
    void testDeleteLeaderboardEntry_NotFound() throws Exception {
        doThrow(new LeaderboardNotFoundException(leaderboardId)).when(leaderboardService).delete(leaderboardId);

        mockMvc.perform(delete("/api/leaderboard/{id}", leaderboardId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Leaderboard entry not found with id: " + leaderboardId));
    }

    @Test
    void testGetSortedLeaderboard() throws Exception {
        Leaderboard lb2 = new Leaderboard(UUID.randomUUID(), user, 50, 2);

        LeaderboardDTO lb2DTO = new LeaderboardDTO();
        lb2DTO.setId(lb2.getId());
        lb2DTO.setUserId(userId);
        lb2DTO.setTotalPoints(50);
        lb2DTO.setRank(2);


        when(leaderboardService.getSorted()).thenReturn(List.of(leaderboard, lb2));
        when(leaderboardMapper.toDTO(leaderboard)).thenReturn(leaderboardDTO);
        when(leaderboardMapper.toDTO(lb2)).thenReturn(lb2DTO);

        mockMvc.perform(get("/api/leaderboard/sorted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].totalPoints", is(100)))
                .andExpect(jsonPath("$[1].totalPoints", is(50)));
    }
}