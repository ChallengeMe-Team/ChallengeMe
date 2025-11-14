package challengeme.backend.controller;

import challengeme.backend.exception.LeaderboardNotFoundException;
import challengeme.backend.model.Leaderboard;
import challengeme.backend.service.LeaderboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaderboardController.class)
@ActiveProfiles("test")
@Import(challengeme.backend.config.TestSecurityConfig.class)
public class LeaderboardControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LeaderboardService leaderboardService;

    @Test
    void testGetAllLeaderboardEntries() throws Exception {
        Leaderboard lb1 = new Leaderboard(UUID.randomUUID(), null, 100, 1);
        Leaderboard lb2 = new Leaderboard(UUID.randomUUID(), null, 50, 2);

        when(leaderboardService.getAll()).thenReturn(List.of(lb1, lb2));

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(leaderboardService, times(1)).getAll();
    }

    @Test
    void testGetLeaderboardEntryById() throws Exception {
        UUID id = UUID.randomUUID();
        Leaderboard lb = new Leaderboard(id, null, 120, 1);

        when(leaderboardService.get(id)).thenReturn(lb);

        mockMvc.perform(get("/api/leaderboard/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.totalPoints").value(120));
    }

    @Test
    void testCreateLeaderboardEntry() throws Exception {
        UUID userId = UUID.randomUUID();
        LeaderboardController.CreateRequest req = new LeaderboardController.CreateRequest();
        req.userId = userId;
        req.totalPoints = 200;

        Leaderboard created = new Leaderboard(UUID.randomUUID(), null, 200, 1);
        when(leaderboardService.create(userId, 200)).thenReturn(created);

        mockMvc.perform(post("/api/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPoints").value(200));

        verify(leaderboardService, times(1)).create(userId, 200);
    }

    @Test
    void testUpdateLeaderboardEntry() throws Exception {
        UUID id = UUID.randomUUID();
        LeaderboardController.UpdateRequest req = new LeaderboardController.UpdateRequest();
        req.totalPoints = 300;

        Leaderboard updated = new Leaderboard(id, null, 300, 1);
        when(leaderboardService.update(id, 300)).thenReturn(updated);

        mockMvc.perform(put("/api/leaderboard/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints").value(300));

        verify(leaderboardService, times(1)).update(id, 300);
    }

    @Test
    void testDeleteLeaderboardEntry() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(leaderboardService).delete(id);

        mockMvc.perform(delete("/api/leaderboard/{id}", id))
                .andExpect(status().isNoContent());

        verify(leaderboardService, times(1)).delete(id);
    }

    @Test
    void testGetLeaderboardEntryById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(leaderboardService.get(id)).thenThrow(new LeaderboardNotFoundException("Not found"));

        mockMvc.perform(get("/api/leaderboard/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    void testUpdateLeaderboardEntry_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        LeaderboardController.UpdateRequest req = new LeaderboardController.UpdateRequest();
        req.totalPoints = 200;

        when(leaderboardService.update(id, 200))
                .thenThrow(new LeaderboardNotFoundException("Not found"));

        mockMvc.perform(put("/api/leaderboard/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    void testDeleteLeaderboardEntry_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new LeaderboardNotFoundException("Not found")).when(leaderboardService).delete(id);

        mockMvc.perform(delete("/api/leaderboard/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    void testGetSortedLeaderboard() throws Exception {
        Leaderboard lb1 = new Leaderboard(UUID.randomUUID(), null, 150, 1);
        Leaderboard lb2 = new Leaderboard(UUID.randomUUID(), null, 120, 2);

        when(leaderboardService.getSorted()).thenReturn(List.of(lb1, lb2));

        mockMvc.perform(get("/api/leaderboard/sorted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].totalPoints").value(150))
                .andExpect(jsonPath("$[1].totalPoints").value(120));
    }

}
