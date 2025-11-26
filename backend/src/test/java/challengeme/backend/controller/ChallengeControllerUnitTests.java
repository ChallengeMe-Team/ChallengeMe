package challengeme.backend.controller;

import challengeme.backend.dto.ChallengeDTO;
import challengeme.backend.dto.request.create.ChallengeCreateRequest;
import challengeme.backend.dto.request.update.ChallengeUpdateRequest;
import challengeme.backend.exception.ChallengeNotFoundException;
import challengeme.backend.exception.GlobalExceptionHandler;
import challengeme.backend.mapper.ChallengeMapper;
import challengeme.backend.model.Challenge;
import challengeme.backend.model.Challenge.Difficulty;
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
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class ChallengeControllerUnitTests {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ChallengeService challengeService;

    @Mock
    private ChallengeMapper challengeMapper;

    @InjectMocks
    private ChallengeController challengeController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(challengeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ChallengeDTO createDTO(UUID id, Challenge entity) {
        return new ChallengeDTO(
                id,
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getDifficulty(),
                entity.getPoints(),
                entity.getCreatedBy()
        );
    }


    // GET /api/challenges
    @Test
    void testGetAllChallenges() throws Exception {
        Challenge c1 = new Challenge(null,"Title1", "Desc1", "Category1", Difficulty.EASY, 100, "user1");
        Challenge c2 = new Challenge(null, "Title2", "Desc2", "Category2", Difficulty.MEDIUM, 200, "user2");

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        ChallengeDTO dto1 = createDTO(uuid1, c1);
        ChallengeDTO dto2 = createDTO(uuid2, c2);

        when(challengeService.getAllChallenges()).thenReturn(List.of(c1, c2));
        when(challengeMapper.toDTO(c1)).thenReturn(dto1);
        when(challengeMapper.toDTO(c2)).thenReturn(dto2);

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
        Challenge challenge = new Challenge( null, "Test", "Desc", "Fitness", Difficulty.HARD, 300, "user123");

        ChallengeDTO dto = createDTO(id, challenge);

        when(challengeService.getChallengeById(id)).thenReturn(challenge);
        when(challengeMapper.toDTO(challenge)).thenReturn(dto);

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
                .andExpect(jsonPath("$.message").value("Challenge with id " + id + " not found"));

        verify(challengeService, times(1)).getChallengeById(id);
    }

    // POST /api/challenges
    @Test
    void testCreateChallenge() throws Exception {
        UUID returnedId = UUID.randomUUID();
        final int newPoints = 150;

        ChallengeCreateRequest request = new ChallengeCreateRequest(
                "New Challenge", "Description", "Fitness", Difficulty.MEDIUM, newPoints, "user123"
        );

        Challenge entityToSave = new Challenge(null, "New Challenge", "Description", "Fitness", Difficulty.MEDIUM, newPoints, "user123");
        Challenge savedEntity = new Challenge(null, "New Challenge", "Description", "Fitness", Difficulty.MEDIUM, newPoints, "user123");

        ChallengeDTO dto = createDTO(returnedId, savedEntity);

        when(challengeMapper.toEntity(any(ChallengeCreateRequest.class))).thenReturn(entityToSave);
        when(challengeService.addChallenge(any(Challenge.class))).thenReturn(savedEntity);
        when(challengeMapper.toDTO(savedEntity)).thenReturn(dto);

        mockMvc.perform(post("/api/challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Challenge"))
                .andExpect(jsonPath("$.category").value("Fitness"))
                .andExpect(jsonPath("$.id").value(returnedId.toString()));

        verify(challengeService, times(1)).addChallenge(any(Challenge.class));
    }

    @Test
    void testCreateChallengeBadRequest_ServiceException() throws Exception {
        ChallengeCreateRequest invalidRequest = new ChallengeCreateRequest(
                "Test title", "Test desc", "Fitness", Difficulty.EASY, 10, "user1"
        );
        Challenge entityToSave = new Challenge(null, "Test title", "Test desc", "Fitness", Difficulty.EASY, 10, "user1");

        when(challengeMapper.toEntity(any(ChallengeCreateRequest.class))).thenReturn(entityToSave);
        when(challengeService.addChallenge(any(Challenge.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/api/challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"));


        verify(challengeService, times(1)).addChallenge(any(Challenge.class));
    }

    @Test
    void testCreateChallengeValidationFailure() throws Exception {
        ChallengeCreateRequest invalidRequest = new ChallengeCreateRequest(
                null, null, "Category", Difficulty.EASY, 10, ""
        );

        mockMvc.perform(post("/api/challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());

        verify(challengeService, never()).addChallenge(any());
    }


    // PUT /api/challenges/{id}
    @Test
    void testUpdateChallenge() throws Exception {
        UUID id = UUID.randomUUID();
        final int updatedPoints = 400;

        ChallengeUpdateRequest request = new ChallengeUpdateRequest(
                "Updated Title", null, "Coding", null, updatedPoints, null
        );

        Challenge updatedEntity = new Challenge(null, "Updated Title", "New Desc",
                "Coding", Difficulty.HARD, updatedPoints, "userX");

        ChallengeDTO dto = createDTO(id, updatedEntity);

        when(challengeService.updateChallenge(eq(id), any(ChallengeUpdateRequest.class))).thenReturn(updatedEntity);
        when(challengeMapper.toDTO(updatedEntity)).thenReturn(dto);

        mockMvc.perform(put("/api/challenges/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.category").value("Coding"))
                .andExpect(jsonPath("$.points", is(updatedPoints)));

        verify(challengeService, times(1)).updateChallenge(eq(id), any(ChallengeUpdateRequest.class));
    }

    @Test
    void testUpdateChallengeNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        ChallengeUpdateRequest request = new ChallengeUpdateRequest(
                "Title", null, null, null, null, null
        );

        when(challengeService.updateChallenge(eq(id), any(ChallengeUpdateRequest.class)))
                .thenThrow(new ChallengeNotFoundException(id));

        mockMvc.perform(put("/api/challenges/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Challenge with id " + id + " not found"));

        verify(challengeService, times(1)).updateChallenge(eq(id), any(ChallengeUpdateRequest.class));
    }

    @Test
    void testUpdateChallengeBadRequest_ServiceException() throws Exception {
        UUID id = UUID.randomUUID();
        ChallengeUpdateRequest request = new ChallengeUpdateRequest(
                null, null, null, null, 10, null
        );

        when(challengeService.updateChallenge(eq(id), any(ChallengeUpdateRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(put("/api/challenges/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"));

        verify(challengeService, times(1)).updateChallenge(eq(id), any(ChallengeUpdateRequest.class));
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
                .andExpect(jsonPath("$.message").value("Challenge with id " + id + " not found"));

        verify(challengeService, times(1)).deleteChallenge(id);
    }
}