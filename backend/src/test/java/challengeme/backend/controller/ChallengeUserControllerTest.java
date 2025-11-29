package challengeme.backend.controller;

import challengeme.backend.exception.GlobalExceptionHandler;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.ChallengeUserStatus;
import challengeme.backend.model.CreateChallengeUserRequest;
import challengeme.backend.model.UpdateChallengeStatusRequest;
import challengeme.backend.service.ChallengeUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
public class ChallengeUserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChallengeUserService challengeUserService;

    @InjectMocks
    private ChallengeUserController challengeUserController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders
                .standaloneSetup(challengeUserController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Test
    void testCreateChallengeUserLink() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        CreateChallengeUserRequest request = new CreateChallengeUserRequest();
        request.setUserId(userId);
        request.setChallengeId(challengeId);

        ChallengeUser mockResponse = new ChallengeUser(UUID.randomUUID(), userId, challengeId, ChallengeUserStatus.PENDING, null, null);

        when(challengeUserService.createChallengeUser(any(CreateChallengeUserRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/user-challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(mockResponse.getId().toString())))
                .andExpect(jsonPath("$.userId", is(userId.toString())))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    void testGetChallengeUserLinksByUserId() throws Exception {
        UUID userId = UUID.randomUUID();
        ChallengeUser cu1 = new ChallengeUser(UUID.randomUUID(), userId, UUID.randomUUID(), ChallengeUserStatus.PENDING, null, null);
        List<ChallengeUser> mockList = List.of(cu1);

        when(challengeUserService.getChallengeUsersByUserId(userId)).thenReturn(mockList);

        mockMvc.perform(get("/api/user-challenges/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(userId.toString())));
    }

    @Test
    void testUpdateChallengeUserLinkStatus() throws Exception {
        UUID linkId = UUID.randomUUID();
        UpdateChallengeStatusRequest request = new UpdateChallengeStatusRequest();
        request.setStatus(ChallengeUserStatus.COMPLETED);

        ChallengeUser mockResponse = new ChallengeUser(linkId, UUID.randomUUID(), UUID.randomUUID(), ChallengeUserStatus.COMPLETED, LocalDate.now(), LocalDate.now());

        when(challengeUserService.updateChallengeUserStatus(eq(linkId), eq(ChallengeUserStatus.COMPLETED))).thenReturn(mockResponse);

        mockMvc.perform(put("/api/user-challenges/{id}/status", linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.id", is(linkId.toString())));
    }

    @Test
    void testDeleteChallengeUserLink() throws Exception {
        UUID linkId = UUID.randomUUID();
        doNothing().when(challengeUserService).deleteChallengeUser(linkId);

        mockMvc.perform(delete("/api/user-challenges/{id}", linkId))
                .andExpect(status().isNoContent());
    }
}