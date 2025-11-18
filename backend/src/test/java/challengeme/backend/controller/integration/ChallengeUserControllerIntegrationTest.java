package challengeme.backend.controller.integration;

import challengeme.backend.BackendApplication;
import challengeme.backend.model.Challenge;
import challengeme.backend.model.ChallengeUserStatus;
import challengeme.backend.model.User;
import challengeme.backend.repository.ChallengeRepository;
import challengeme.backend.repository.ChallengeUserRepository;
import challengeme.backend.repository.UserRepository;
import challengeme.backend.dto.request.create.ChallengeUserCreateRequest;
import challengeme.backend.dto.request.update.ChallengeUserUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Integration tests cu TestRestTemplate – testează endpoint-urile HTTP reale, cu Spring context complet.

@SpringBootTest(classes = BackendApplication.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc
public class ChallengeUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChallengeUserRepository challengeUserRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Challenge testChallenge;

    @BeforeEach
    void setupDependencies() {
        challengeUserRepository.deleteAll();
        userRepository.deleteAll();
        challengeRepository.deleteAll();

        User u = new User(null, "IntegrationTestUser", "itest@email.com", "secure_pass_it", 0);
        testUser = userRepository.save(u);

        Challenge c = new Challenge(null, "Integration Challenge", "Integration Desc", "IT", Challenge.Difficulty.EASY, 50, "ITCreator");
        testChallenge = challengeRepository.save(c);
    }

    @AfterEach
    void tearDown() {
        challengeUserRepository.deleteAll();
        userRepository.deleteAll();
        challengeRepository.deleteAll();
    }

    @Test
    void testFullCrudLifecycle() throws Exception {
        UUID userId = testUser.getId();
        UUID challengeId = testChallenge.getId();

        ChallengeUserCreateRequest createRequest = new ChallengeUserCreateRequest();
        createRequest.setUserId(userId);
        createRequest.setChallengeId(challengeId);

        // CREATE
        MvcResult createResult = mockMvc.perform(post("/api/user-challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.challengeId").value(challengeId.toString()))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        String newLinkIdStr = objectMapper.readTree(responseJson).get("id").asText();
        UUID newLinkId = UUID.fromString(newLinkIdStr);

        mockMvc.perform(get("/api/user-challenges/{id}", newLinkId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newLinkIdStr)));


        // GET BY USER
        mockMvc.perform(get("/api/user-challenges/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(newLinkIdStr)));

        // UPDATE STATUS
        ChallengeUserUpdateRequest updateRequest = new ChallengeUserUpdateRequest();
        updateRequest.setStatus(ChallengeUserStatus.COMPLETED);

        mockMvc.perform(put("/api/user-challenges/{id}/status", newLinkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.dateCompleted").isNotEmpty());

        // DELETE
        mockMvc.perform(delete("/api/user-challenges/{id}", newLinkId))
                .andExpect(status().isNoContent());

        // GET BY ID AFTER DELETE
        mockMvc.perform(get("/api/user-challenges/{id}", newLinkId))
                .andExpect(status().isNotFound());
    }
}