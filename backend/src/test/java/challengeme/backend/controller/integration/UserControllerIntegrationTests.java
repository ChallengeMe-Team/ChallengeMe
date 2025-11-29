package challengeme.backend.controller.integration;

import challengeme.backend.model.User;
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

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Integration tests cu TestRestTemplate – testează endpoint-urile HTTP reale, cu Spring context complet.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.test.context.ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
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
        User user = new User(null, "Ana", "ana@email.com", "Password_123", 10, "user");

        // POST
        ResponseEntity<User> postResponse = restTemplate.postForEntity(baseUrl, user, User.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        UUID id = Objects.requireNonNull(postResponse.getBody()).getId();

        // GET
        ResponseEntity<User> getResponse = restTemplate.getForEntity(baseUrl + "/" + id, User.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getUsername()).isEqualTo("Ana");

        // Curățare
        userService.deleteUser(id);
    }

    @Test
    void testUpdateUser() {
        User initial = new User(null, "Ion", "ion@email.com", "Password_123", 5, "user");
        User saved = userService.createUser(initial);

        User updatedBody = new User(null, "IonUpdated", "ionupdated@email.com", "NewPassword_123", 10,"user");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> entity = new HttpEntity<>(updatedBody, headers);

        // PUT/Exchange
        ResponseEntity<User> putResponse = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                entity,
                User.class
        );

        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(putResponse.getBody().getUsername()).isEqualTo("IonUpdated");

        userService.deleteUser(saved.getId());
    }

    @Test
    void testDeleteUser() {
        User user = new User(null, "Maria", "maria@email.com", "Password_123", 8,"user");
        User saved = userService.createUser(user);

        // DELETE
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl + "/" + saved.getId(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}