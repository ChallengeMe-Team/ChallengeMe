package challengeme.backend.controller.integration;

import challengeme.backend.model.User;
import challengeme.backend.service.UserService;
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
public class UserControllerIntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/api/users";
    }

    @Test
    void testCreateAndGetUser() {
        User user = new User(null, "Ana", "ana@email.com", "pass123", 10);
        ResponseEntity<User> postResponse = restTemplate.postForEntity(baseUrl, user, User.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        UUID id = Objects.requireNonNull(postResponse.getBody()).getId();
        ResponseEntity<User> getResponse = restTemplate.getForEntity(baseUrl + "/" + id, User.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getUsername()).isEqualTo("Ana");

        userService.deleteUser(id);
    }

    @Test
    void testUpdateUser() {
        User user = new User(null, "Ion", "ion@email.com", "pass123", 5);
        User saved = userService.createUser(user);

        User updated = new User(null, "IonUpdated", "ionupdated@email.com", "newpass", 10);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> entity = new HttpEntity<>(updated, headers);

        ResponseEntity<User> putResponse = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                entity,
                User.class
        );
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(putResponse.getBody().getUsername()).isEqualTo("IonUpdated");

        ResponseEntity<User> getResponse = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), User.class);
        assertThat(getResponse.getBody().getUsername()).isEqualTo("IonUpdated");

        userService.deleteUser(saved.getId());
    }

    @Test
    void testDeleteUser() {
        User user = new User(null, "Maria", "maria@email.com", "pass123", 8);
        User saved = userService.createUser(user);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).contains("User with id " + saved.getId() + " not found");
    }
}
