package challengeme.backend.controller.integration;

import challengeme.backend.BackendApplication;
import challengeme.backend.model.Challenge;
import challengeme.backend.model.ChallengeUserStatus;
import challengeme.backend.model.User;
import challengeme.backend.repository.*; // Importam toate repository-urile
import challengeme.backend.dto.request.create.ChallengeUserCreateRequest;
import challengeme.backend.dto.request.update.ChallengeUserUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// Integration tests cu TestRestTemplate – testează endpoint-urile HTTP reale, cu Spring context complet.

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
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
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Challenge testChallenge;

    @BeforeEach
    void setupDependencies() {
        // 1. Curatam tabelele DEPENDENTE (Copii)
        // Ordinea e critica: sterge intai ce depinde de User/Challenge
        leaderboardRepository.deleteAll();      // Fix pentru eroarea ta actuala
        userBadgeRepository.deleteAll();
        notificationRepository.deleteAll();
        challengeUserRepository.deleteAll();

        // 2. Curatam tabelele PRINCIPALE (Parinti)
        userRepository.deleteAll();
        challengeRepository.deleteAll();
        badgeRepository.deleteAll();

        // 3. Populare date de test
        User u = new User(null, "IntegrationTestUser", "itest@email.com", "secure_pass_it", 0, "user");
        testUser = userRepository.save(u);

        Challenge c = new Challenge(null, "Integration Challenge", "Integration Desc", "IT", Challenge.Difficulty.EASY, 50, "ITCreator");
        testChallenge = challengeRepository.save(c);
    }

    @AfterEach
    void tearDown() {
        // Repetam curatarea la final pentru a lasa DB-ul curat
        leaderboardRepository.deleteAll();
        userBadgeRepository.deleteAll();
        notificationRepository.deleteAll();
        challengeUserRepository.deleteAll();

        userRepository.deleteAll();
        challengeRepository.deleteAll();
        badgeRepository.deleteAll();
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

        // GET BY ID
        mockMvc.perform(get("/api/user-challenges/{id}", newLinkIdStr))
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

        mockMvc.perform(put("/api/user-challenges/{id}/status", newLinkIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.dateCompleted").isNotEmpty());

        // DELETE
        mockMvc.perform(delete("/api/user-challenges/{id}", newLinkIdStr))
                .andExpect(status().isNoContent());

        // GET BY ID AFTER DELETE
        mockMvc.perform(get("/api/user-challenges/{id}", newLinkIdStr))
                .andExpect(status().isNotFound());
    }
}