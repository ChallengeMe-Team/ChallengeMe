package challengeme.backend.controller.integration;

import challengeme.backend.model.User;
import challengeme.backend.service.LeaderboardService;
import challengeme.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Integration tests cu TestRestTemplate – testează endpoint-urile HTTP reale, cu Spring context complet.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.test.context.ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class LeaderboardControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private LeaderboardService leaderboardService;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/api/leaderboard";
    }

    private UUID createUser(String name) {
        User user = new User(null, name, name + "@email.com", "secret12", 0, "user");
        return userService.createUser(user).getId();
    }

    @Test
    void testCreateAndGetLeaderboardEntry() {
        UUID userId = createUser("ana");

        Map<String, Object> body = Map.of("userId", userId.toString(), "totalPoints", 120);
        ResponseEntity<Map> postResponse = restTemplate.postForEntity(baseUrl, body, Map.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String id = Objects.requireNonNull(postResponse.getBody()).get("id").toString();

        ResponseEntity<Map> getResponse = restTemplate.getForEntity(baseUrl + "/" + id, Map.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().get("totalPoints")).isEqualTo(120);
    }

    @Test
    void testDeleteLeaderboardEntry() {
        UUID userId = createUser("daria");

        // Creăm intrarea direct prin service
        var created = leaderboardService.create(userId, 10);
        String id = created.getId().toString();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl + "/" + id, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetLeaderboardEntryById_NotFound() {
        UUID invalidId = UUID.randomUUID();

        ResponseEntity<Map> getResponse = restTemplate.getForEntity(baseUrl + "/" + invalidId, Map.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).containsKey("message");
    }
}