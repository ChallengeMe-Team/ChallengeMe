package challengeme.backend.controller.integration;

import challengeme.backend.model.Badge;
import challengeme.backend.service.BadgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BadgeControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BadgeService badgeService;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/api/badges";
    }

    @Test
    void testCreateAndGetBadge() {
        Badge badge = new Badge(null, "Explorer", "Visited 5 locations", "Visit 5 locations");
        ResponseEntity<Badge> postResponse = restTemplate.postForEntity(baseUrl, badge, Badge.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        UUID id = Objects.requireNonNull(postResponse.getBody()).getId();
        ResponseEntity<Badge> getResponse = restTemplate.getForEntity(baseUrl + "/" + id, Badge.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo("Explorer");

        badgeService.deleteBadge(id);
    }

    @Test
    void testUpdateBadge() {
        Badge badge = new Badge(null, "Achiever", "Completed all tasks", "Complete all challenges");
        Badge saved = badgeService.createBadge(badge);

        Badge updated = new Badge(null, "Achiever Updated", "Completed 10 tasks", "Complete 10 challenges");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Badge> entity = new HttpEntity<>(updated, headers);

        ResponseEntity<Badge> putResponse = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                entity,
                Badge.class
        );
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(putResponse.getBody().getName()).isEqualTo("Achiever Updated");

        ResponseEntity<Badge> getResponse = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), Badge.class);
        assertThat(getResponse.getBody().getName()).isEqualTo("Achiever Updated");

        badgeService.deleteBadge(saved.getId());
    }

    @Test
    void testDeleteBadge() {
        Badge badge = new Badge(null, "Explorer", "Visited 5 locations", "Visit 5 locations");
        Badge saved = badgeService.createBadge(badge);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).contains("Badge with id " + saved.getId() + " not found");
    }
}
