package challengeme.backend.controller.integration;

import challengeme.backend.model.User;
import challengeme.backend.service.LeaderboardService;
import challengeme.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.test.context.ActiveProfiles("test")
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
        User u = new User(name, name + "@email.com", "secret12", 0);
        return userService.createUser(u).getId();
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
    void testUpdateLeaderboardEntry() {

        UUID userId = createUser("mihai");
        var created = leaderboardService.create(userId, 50); // ne dă id rapid


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(Map.of("totalPoints", 200), headers);

        ResponseEntity<Map> putResponse = restTemplate.exchange(
                baseUrl + "/" + created.getId(), HttpMethod.PUT, entity, Map.class);

        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(putResponse.getBody().get("totalPoints")).isEqualTo(200);


        ResponseEntity<Map> getResponse =
                restTemplate.getForEntity(baseUrl + "/" + created.getId(), Map.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().get("totalPoints")).isEqualTo(200);


        ResponseEntity<List> sortedResponse =
                restTemplate.getForEntity(baseUrl + "/sorted", List.class);
        assertThat(sortedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map first = (Map) sortedResponse.getBody().get(0);
        assertThat(first.get("id")).isEqualTo(created.getId().toString());
        assertThat(first.get("rank")).isEqualTo(1);
    }

    @Test
    void testDeleteLeaderboardEntry() {
        UUID userId = createUser("daria");
        var created = leaderboardService.create(userId, 10);
        String id = created.getId().toString();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


        ResponseEntity<String> getResponse =
                restTemplate.getForEntity(baseUrl + "/" + id, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }
}
