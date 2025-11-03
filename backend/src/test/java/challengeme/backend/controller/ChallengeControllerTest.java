package challengeme.backend.controller;

import challengeme.backend.domain.Challenge;
import challengeme.backend.exceptions.ChallengeNotFoundException;
import challengeme.backend.service.ChallengeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChallengeController.class)
class ChallengeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChallengeService challengeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllChallenges() throws Exception {
        Challenge challenge = new Challenge(
                "Test", "Desc", "Fitness", Challenge.Difficulty.EASY, 100, "user1"
        );
        when(challengeService.getAllChallenges()).thenReturn(List.of(challenge));

        mockMvc.perform(get("/api/challenges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test"));
    }

    @Test
    void shouldReturnNotFoundWhenChallengeDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(challengeService.getChallengeById(id))
                .thenThrow(new ChallengeNotFoundException(id));

        mockMvc.perform(get("/api/challenges/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetChallengeById() throws Exception {
        UUID id = UUID.randomUUID();
        Challenge challenge = new Challenge(
                "Test", "Desc", "Fitness", Challenge.Difficulty.EASY, 100, "user1"
        );
        challenge.setId(id);

        when(challengeService.getChallengeById(id)).thenReturn(challenge);

        mockMvc.perform(get("/api/challenges/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    void shouldCreateChallenge() throws Exception {
        Challenge challenge = new Challenge(
                "New Challenge", "Description", "Fitness",
                Challenge.Difficulty.MEDIUM, 150, "user123"
        );

        when(challengeService.addChallenge(any(Challenge.class))).thenReturn(challenge);

        mockMvc.perform(post("/api/challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(challenge)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Challenge"));
    }

    @Test
    void shouldUpdateChallenge() throws Exception {
        UUID id = UUID.randomUUID();
        Challenge challenge = new Challenge(
                "Updated Challenge", "Updated Desc", "Fitness",
                Challenge.Difficulty.HARD, 200, "user123"
        );

        when(challengeService.updateChallenge(any(UUID.class), any(Challenge.class))).thenReturn(challenge);

        mockMvc.perform(put("/api/challenges/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(challenge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Challenge"));
    }

    @Test
    void shouldDeleteChallenge() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/challenges/" + id))
                .andExpect(status().isNoContent());
    }
}