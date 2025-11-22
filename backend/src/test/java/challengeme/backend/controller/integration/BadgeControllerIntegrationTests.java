package challengeme.backend.controller.integration;

import challengeme.backend.dto.request.create.BadgeCreateRequest;
import challengeme.backend.dto.request.update.BadgeUpdateRequest;
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

//Integration tests cu TestRestTemplate – testează endpoint-urile HTTP reale, cu Spring context complet.

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
        BadgeCreateRequest request = new BadgeCreateRequest("Explorer", "Visited 5 locations", "Visit 5 locations");
        ResponseEntity<Badge> postResponse = restTemplate.postForEntity(baseUrl, request, Badge.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        UUID id = Objects.requireNonNull(postResponse.getBody()).getId();
        ResponseEntity<Badge> getResponse = restTemplate.getForEntity(baseUrl + "/" + id, Badge.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo("Explorer");

        badgeService.deleteBadge(id);
    }

    @Test
    void testUpdateBadge() {
        BadgeCreateRequest createRequest = new BadgeCreateRequest("Achiever", "Completed all tasks", "Complete all challenges");
        Badge saved = badgeService.createBadge(new Badge(null, createRequest.name(), createRequest.description(), createRequest.criteria()));

        BadgeUpdateRequest updateRequest = new BadgeUpdateRequest("Achiever Updated", "Completed 10 tasks", "Complete 10 challenges");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BadgeUpdateRequest> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<Badge> putResponse = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                entity,
                Badge.class
        );
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(putResponse.getBody().getName()).isEqualTo("Achiever Updated");

        badgeService.deleteBadge(saved.getId());
    }

    @Test
    void testDeleteBadge() {
        BadgeCreateRequest createRequest = new BadgeCreateRequest("Explorer", "Visited 5 locations", "Visit 5 locations");
        Badge saved = badgeService.createBadge(new Badge(null, createRequest.name(), createRequest.description(), createRequest.criteria()));

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
