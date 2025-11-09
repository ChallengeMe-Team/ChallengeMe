package challengeme.backend.controller.integration;

import challengeme.backend.BackendApplication;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.ChallengeUserStatus;
import challengeme.backend.model.CreateChallengeUserRequest;
import challengeme.backend.model.UpdateChallengeStatusRequest;
import challengeme.backend.repository.ChallengeUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BackendApplication.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc
public class ChallengeUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChallengeUserRepository challengeUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        challengeUserRepository.findAll().forEach(cu -> challengeUserRepository.deleteById(cu.getId()));
    }

    @Test
    void testFullCrudLifecycle() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        CreateChallengeUserRequest createRequest = new CreateChallengeUserRequest();
        createRequest.setUserId(userId);
        createRequest.setChallengeId(challengeId);

        MvcResult createResult = mockMvc.perform(post("/api/user-challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(userId.toString())))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andReturn();

        String jsonResponse = createResult.getResponse().getContentAsString();
        ChallengeUser createdLink = objectMapper.readValue(jsonResponse, ChallengeUser.class);
        UUID newLinkId = createdLink.getId();

        mockMvc.perform(get("/api/user-challenges/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(newLinkId.toString())));

        UpdateChallengeStatusRequest updateRequest = new UpdateChallengeStatusRequest();
        updateRequest.setStatus(ChallengeUserStatus.COMPLETED);

        mockMvc.perform(put("/api/user-challenges/{id}/status", newLinkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.dateCompleted").isNotEmpty());

        mockMvc.perform(delete("/api/user-challenges/{id}", newLinkId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/user-challenges/{id}", newLinkId))
                .andExpect(status().isNotFound());
    }
}