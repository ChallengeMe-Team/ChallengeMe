package challengeme.backend.controller.integration;

import challengeme.backend.model.Badge;
import challengeme.backend.model.User;
import challengeme.backend.model.UserBadge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserBadgeController using TestRestTemplate.
 * Verifies real HTTP interaction with the running application context.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserBadgeControllerIntegrationTests {



    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/userbadges";
    }

    @Test
    void testCreateAndGetUserBadge() {
        // Arrange
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com", "secret123", 10);
        Badge badge = new Badge(UUID.randomUUID(), "Gold", "Top performer", "Complete 10 challenges");
        UserBadge userBadge = new UserBadge(null, user, badge, LocalDate.now());

        // Act — POST
        ResponseEntity<UserBadge> postResponse = restTemplate.postForEntity(
                getBaseUrl(), userBadge, UserBadge.class);

        // Assert — created successfully
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());
        assertNotNull(postResponse.getBody().getId());

        UUID createdId = postResponse.getBody().getId();

        // Act — GET newly created
        ResponseEntity<UserBadge> getResponse =
                restTemplate.getForEntity(getBaseUrl() + "/" + createdId, UserBadge.class);

        // Assert — entity exists and matches
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("Ana", getResponse.getBody().getUser().getUsername());
        assertEquals("Gold", getResponse.getBody().getBadge().getName());
    }

    @Test
    void testUpdateUserBadge() {
        // Arrange
        User user = new User(UUID.randomUUID(), "Ion", "ion@email.com", "pass123", 5);
        Badge badge = new Badge(UUID.randomUUID(), "Silver", "Runner-up", "Complete 5 challenges");
        UserBadge userBadge = new UserBadge(null, user, badge, LocalDate.now());

        ResponseEntity<UserBadge> postResponse =
                restTemplate.postForEntity(getBaseUrl(), userBadge, UserBadge.class);

        assertNotNull(postResponse.getBody(), "Response body should not be null");
        UUID id = postResponse.getBody().getId();


        // Modify and PUT
        badge.setName("Platinum");
        userBadge.setBadge(badge);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserBadge> entity = new HttpEntity<>(userBadge, headers);

        ResponseEntity<UserBadge> putResponse =
                restTemplate.exchange(getBaseUrl() + "/" + id, HttpMethod.PUT, entity,
                        new ParameterizedTypeReference<UserBadge>() {});


        assertEquals(HttpStatus.OK, putResponse.getStatusCode());
        assertEquals("Platinum", putResponse.getBody().getBadge().getName());
    }

    @Test
    void testDeleteUserBadge() {
        // Arrange
        User user = new User(UUID.randomUUID(), "Mara", "mara@email.com", "pw1234", 3);
        Badge badge = new Badge(UUID.randomUUID(), "Bronze", "Starter", "Complete 1 challenge");
        UserBadge userBadge = new UserBadge(null, user, badge, LocalDate.now());

        ResponseEntity<UserBadge> postResponse =
                restTemplate.postForEntity(getBaseUrl(), userBadge, UserBadge.class);

        UUID id = postResponse.getBody().getId();

        // Act — DELETE
        restTemplate.delete(getBaseUrl() + "/" + id);

        // Assert — should no longer exist
        ResponseEntity<String> getResponse =
                restTemplate.getForEntity(getBaseUrl() + "/" + id, String.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}
