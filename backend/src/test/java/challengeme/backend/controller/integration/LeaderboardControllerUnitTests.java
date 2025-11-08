package challengeme.backend.controller.integration;

import challengeme.backend.model.User;
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
public class LeaderboardControllerUnitTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseLbUrl;
    private String baseUsersUrl;

    @BeforeEach
    void setup() {
        baseLbUrl    = "http://localhost:" + port + "/api/leaderboard";
        baseUsersUrl = "http://localhost:" + port + "/api/users";
    }

    // ----------------- helpers -----------------
    private UUID createUserAndReturnId(String name) {
        // Stilul tău: User(String username, String email, String password, Integer points)
        User user = new User(name, name + "@email.com", "secret12", 0);
        ResponseEntity<User> post = restTemplate.postForEntity(baseUsersUrl, user, User.class);
        assertThat(post.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return Objects.requireNonNull(post.getBody()).getId();
    }

    private ResponseEntity<Map> postLeaderboard(UUID userId, int points) {
        Map<String, Object> body = Map.of("userId", userId.toString(), "totalPoints", points);
        return restTemplate.postForEntity(baseLbUrl, body, Map.class);
    }

    // ----------------- tests -----------------

    @Test
    void create_and_get_sorted_should_work() {
        UUID userId = createUserAndReturnId("ana");

        // POST /api/leaderboard
        ResponseEntity<Map> createResp = postLeaderboard(userId, 120);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResp.getBody()).isNotNull();
        assertThat(createResp.getBody().get("totalPoints")).isEqualTo(120);

        // GET /api/leaderboard/sorted
        ResponseEntity<List> sortedResp = restTemplate.getForEntity(baseLbUrl + "/sorted", List.class);
        assertThat(sortedResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sortedResp.getBody()).isNotNull();

        Map first = (Map) sortedResp.getBody().get(0);
        assertThat(first.get("totalPoints")).isEqualTo(120);
        assertThat(first.get("rank")).isEqualTo(1);
    }

    @Test
    void update_should_change_points_and_rank() {
        UUID u1 = createUserAndReturnId("ana");
        UUID u2 = createUserAndReturnId("mihai");

        // seed 2 entries
        postLeaderboard(u1, 50);
        postLeaderboard(u2, 30);


        List listBefore = restTemplate.getForEntity(baseLbUrl, List.class).getBody();
        assertThat(listBefore).isNotNull();
        String toUpdateId = (String) ((Map) listBefore.get(1)).get("id");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(Map.of("totalPoints", 200), headers);

        ResponseEntity<Map> updResp = restTemplate.exchange(
                baseLbUrl + "/" + toUpdateId, HttpMethod.PUT, req, Map.class);

        assertThat(updResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(updResp.getBody()).get("totalPoints")).isEqualTo(200);

        List sorted = restTemplate.getForEntity(baseLbUrl + "/sorted", List.class).getBody();
        Map first = (Map) sorted.get(0);
        assertThat(first.get("id")).isEqualTo(toUpdateId);
        assertThat(first.get("rank")).isEqualTo(1);
    }

    @Test
    void delete_should_return_204_and_remove_entry() {
        UUID userId = createUserAndReturnId("daria");

        Map created = restTemplate.postForEntity(
                baseLbUrl, Map.of("userId", userId.toString(), "totalPoints", 10), Map.class
        ).getBody();
        assertThat(created).isNotNull();
        String id = (String) created.get("id");

        // DELETE /api/leaderboard/{id}
        restTemplate.delete(baseLbUrl + "/" + id);

        // GET all -> id-ul nu trebuie să mai fie prezent
        List all = restTemplate.getForEntity(baseLbUrl, List.class).getBody();
        assertThat(all).extracting("id").doesNotContain(id);
    }
}
