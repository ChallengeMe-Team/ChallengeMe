package challengeme.backend.controller;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.dto.request.create.ChallengeUserCreateRequest;
import challengeme.backend.dto.request.update.ChallengeUserUpdateRequest;
import challengeme.backend.exception.GlobalExceptionHandler;
import challengeme.backend.mapper.ChallengeUserMapper;
import challengeme.backend.model.Challenge;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.ChallengeUserStatus;
import challengeme.backend.model.User;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

//Unit tests cu MockMvc – testează controller-ul izolat, folosind mock pentru ChallengeUserService.

@ExtendWith(MockitoExtension.class)
public class ChallengeUserControllerUnitTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ChallengeUserService challengeUserService;

    @Mock
    private ChallengeUserMapper challengeUserMapper;

    @InjectMocks
    private ChallengeUserController challengeUserController;

    private UUID userId;
    private UUID challengeId;
    private UUID linkId;
    private User user;
    private Challenge challenge;
    private ChallengeUser challengeUser;
    private ChallengeUserDTO challengeUserDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(challengeUserController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userId = UUID.randomUUID();
        challengeId = UUID.randomUUID();
        linkId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setUsername("TestUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setPoints(0);

        challenge = new Challenge();
        challenge.setId(challengeId);
        challenge.setTitle("Test Challenge");
        challenge.setDescription("Description");

        challengeUser = new ChallengeUser();
        challengeUser.setId(linkId);
        challengeUser.setUser(user);
        challengeUser.setChallenge(challenge);
        challengeUser.setStatus(ChallengeUserStatus.PENDING);

        challengeUserDTO = new ChallengeUserDTO();
        challengeUserDTO.setId(linkId);
        challengeUserDTO.setUserId(userId);
        challengeUserDTO.setChallengeId(challengeId);
        challengeUserDTO.setStatus(ChallengeUserStatus.PENDING);
    }

    @Test
    void testCreateChallengeUser() throws Exception {
        ChallengeUserCreateRequest request = new ChallengeUserCreateRequest();
        request.setUserId(userId);
        request.setChallengeId(challengeId);

        when(challengeUserService.createChallengeUser(any(ChallengeUserCreateRequest.class))).thenReturn(challengeUser);
        when(challengeUserMapper.toDTO(challengeUser)).thenReturn(challengeUserDTO);

        mockMvc.perform(post("/api/user-challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(linkId.toString())))
                .andExpect(jsonPath("$.userId", is(userId.toString())))
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(challengeUserService).createChallengeUser(any(ChallengeUserCreateRequest.class));
    }

    @Test
    void testGetChallengeUsersByUserId() throws Exception {
        when(challengeUserService.getChallengeUsersByUserId(userId)).thenReturn(List.of(challengeUser));
        when(challengeUserMapper.toDTO(challengeUser)).thenReturn(challengeUserDTO);

        mockMvc.perform(get("/api/user-challenges/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(userId.toString())));
    }

    @Test
    void testUpdateChallengeUserStatus() throws Exception {
        ChallengeUserUpdateRequest request = new ChallengeUserUpdateRequest();
        request.setStatus(ChallengeUserStatus.COMPLETED);

        ChallengeUser updated = new ChallengeUser();
        updated.setId(linkId);
        updated.setUser(user);
        updated.setChallenge(challenge);
        updated.setStatus(ChallengeUserStatus.COMPLETED);
        updated.setDateCompleted(LocalDate.now());

        ChallengeUserDTO updatedDTO = new ChallengeUserDTO();
        updatedDTO.setId(linkId);
        updatedDTO.setUserId(userId);
        updatedDTO.setChallengeId(challengeId);
        updatedDTO.setStatus(ChallengeUserStatus.COMPLETED);
        updatedDTO.setDateCompleted(LocalDate.now());

        when(challengeUserService.updateChallengeUserStatus(eq(linkId), eq(ChallengeUserStatus.COMPLETED))).thenReturn(updated);
        when(challengeUserMapper.toDTO(updated)).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/user-challenges/{id}/status", linkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.id", is(linkId.toString())));
    }


    @Test
    void testDeleteChallengeUser() throws Exception {
        doNothing().when(challengeUserService).deleteChallengeUser(linkId);

        mockMvc.perform(delete("/api/user-challenges/{id}", linkId))
                .andExpect(status().isNoContent());

        verify(challengeUserService).deleteChallengeUser(linkId);
    }
}
