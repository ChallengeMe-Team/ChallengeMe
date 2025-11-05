package challengeme.backend.controller;

import challengeme.backend.domain.Challenge;
import challengeme.backend.exceptions.ChallengeNotFoundException;
import challengeme.backend.exceptions.GlobalExceptionHandler;
import challengeme.backend.service.ChallengeService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ChallengeController
 * using MockMvc standalone setup and Mockito mocks for ChallengeService.
 */
@ExtendWith(MockitoExtension.class)
class ChallengeControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ChallengeService challengeService;

    @InjectMocks
    private ChallengeController challengeController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(challengeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // GET /api/challenges
    @Test
    void testGetAllChallenges() throws Exception {
        Challenge c1 = new Challenge("Title1", "Desc1", "Category1", Challenge.Difficulty.EASY, 100, "user1");
        Challenge c2 = new Challenge("Title2", "Desc2", "Category2", Challenge.Difficulty.MEDIUM, 200, "user2");

        when(challengeService.getAllChallenges()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/challenges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[1].title").value("Title2"));

        verify(challengeService, times(1)).getAllChallenges();
    }

    // GET /api/challenges/{id}
    @Test
    void testGetChallengeById() throws Exception {
        UUID id = UUID.randomUUID();
        Challenge challenge = new Challenge("Test", "Desc", "Fitness", Challenge.Difficulty.HARD, 300, "user123");
        challenge.setId(id);

        when(challengeService.getChallengeById(id)).thenReturn(challenge);

        mockMvc.perform(get("/api/challenges/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test"))
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(challengeService, times(1)).getChallengeById(id);
    }

    @Test
    void testGetChallengeByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(challengeService.getChallengeById(id))
                .thenThrow(new ChallengeNotFoundException(id));

        mockMvc.perform(get("/api/challenges/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Challenge with id " + id + " not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(challengeService, times(1)).getChallengeById(id);
    }

    // POST /api/challenges
    @Test
    void testCreateChallenge() throws Exception {
        Challenge challenge = new Challenge(null, "New Challenge", "Description",
                "Fitness", Challenge.Difficulty.MEDIUM, 150, "user123");
        Challenge created = new Challenge("New Challenge", "Description",
                "Fitness", Challenge.Difficulty.MEDIUM, 150, "user123");

        when(challengeService.addChallenge(any(Challenge.class))).thenReturn(created);

        mockMvc.perform(post("/api/challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(challenge)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Challenge"))
                .andExpect(jsonPath("$.category").value("Fitness"));

        verify(challengeService, times(1)).addChallenge(any(Challenge.class));
    }

    @Test
    void testCreateChallengeBadRequest() throws Exception {
        Challenge invalid = new Challenge(
                "Test title", "Test desc", "Fitness",
                Challenge.Difficulty.EASY, 10, "user1"
        );
        when(challengeService.addChallenge(any(Challenge.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/api/challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(challengeService, times(1)).addChallenge(any(Challenge.class));
    }

    // PUT /api/challenges/{id}
    @Test
    void testUpdateChallenge() throws Exception {
        UUID id = UUID.randomUUID();
        Challenge update = new Challenge(null, "Updated Title", "New Desc",
                "Coding", Challenge.Difficulty.HARD, 400, "userX");
        Challenge updated = new Challenge("Updated Title", "New Desc",
                "Coding", Challenge.Difficulty.HARD, 400, "userX");

        when(challengeService.updateChallenge(eq(id), any(Challenge.class))).thenReturn(updated);

        mockMvc.perform(put("/api/challenges/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.category").value("Coding"));

        verify(challengeService, times(1)).updateChallenge(eq(id), any(Challenge.class));
    }

    @Test
    void testUpdateChallengeNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Challenge update = new Challenge("Title", "Desc", "C", Challenge.Difficulty.EASY, 100, "U");

        when(challengeService.updateChallenge(eq(id), any(Challenge.class)))
                .thenThrow(new ChallengeNotFoundException(id));

        mockMvc.perform(put("/api/challenges/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Challenge with id " + id + " not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(challengeService, times(1)).updateChallenge(eq(id), any(Challenge.class));
    }

    @Test
    void testUpdateChallengeBadRequest() throws Exception {
        UUID id = UUID.randomUUID();
        Challenge invalid = new Challenge(
                "Test title", "Test desc", "Fitness",
                Challenge.Difficulty.EASY, 10, "user1"
        );

        // Correctly mock updateChallenge (not addChallenge)
        when(challengeService.updateChallenge(eq(id), any(Challenge.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(put("/api/challenges/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(challengeService, times(1)).updateChallenge(eq(id), any(Challenge.class));
    }

    // DELETE /api/challenges/{id}
    @Test
    void testDeleteChallenge() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(challengeService).deleteChallenge(id);

        mockMvc.perform(delete("/api/challenges/{id}", id))
                .andExpect(status().isNoContent());

        verify(challengeService, times(1)).deleteChallenge(id);
    }

    @Test
    void testDeleteChallengeNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ChallengeNotFoundException(id)).when(challengeService).deleteChallenge(id);

        mockMvc.perform(delete("/api/challenges/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Challenge with id " + id + " not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(challengeService, times(1)).deleteChallenge(id);
    }
}
