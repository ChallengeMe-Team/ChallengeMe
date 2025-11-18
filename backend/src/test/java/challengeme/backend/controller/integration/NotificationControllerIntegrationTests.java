package challengeme.backend.controller.integration;

import challengeme.backend.model.Notification;
import challengeme.backend.model.NotificationType;
import challengeme.backend.repository.NotificationRepository;
import challengeme.backend.dto.request.create.NotificationCreateRequest;
import challengeme.backend.dto.request.update.NotificationUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Importăm configurația de securitate de test
@Import(NotificationControllerIntegrationTests.TestSecurityConfig.class)
class NotificationControllerIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NotificationRepository repository;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .csrf(csrf -> csrf.disable());
            return http.build();
        }
    }

    @AfterEach
    void tearDown() {
        repository.findAll().forEach(n -> repository.deleteById(n.getId()));
    }

    @Test
    void testCreateAndGetNotification() {
        UUID userId = UUID.randomUUID();

        NotificationCreateRequest request = new NotificationCreateRequest(
                userId,
                "Integration Test",
                NotificationType.CHALLENGE
        );

        // POST
        ResponseEntity<Notification> createResponse = restTemplate.postForEntity("/api/notifications", request, Notification.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        UUID createdId = createResponse.getBody().getId();

        // GET
        ResponseEntity<Notification> getResponse = restTemplate.getForEntity("/api/notifications/{id}", Notification.class, createdId);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getId()).isEqualTo(createdId);
        assertThat(getResponse.getBody().getUserId()).isEqualTo(userId);
    }

    @Test
    void testUpdateNotification() {
        UUID userId = UUID.randomUUID();

        Notification initial = new Notification(null, userId, "Msg", NotificationType.SYSTEM, LocalDateTime.now(), false);
        Notification saved = repository.save(initial);

        NotificationUpdateRequest updateRequest = new NotificationUpdateRequest(true);

        RequestEntity<NotificationUpdateRequest> requestEntity = RequestEntity
                .patch("/api/notifications/{id}", saved.getId())
                .body(updateRequest);

        ResponseEntity<Void> patchResponse = restTemplate.exchange(requestEntity, Void.class);
        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Notification updated = repository.findById(saved.getId()).orElseThrow();
        assertThat(updated.isRead()).isTrue();
    }

    @Test
    void testDeleteNotification() {
        UUID userId = UUID.randomUUID();
        Notification initial = new Notification(null, userId, "Msg", NotificationType.SYSTEM, LocalDateTime.now(), false);
        Notification saved = repository.save(initial);

        restTemplate.delete("/api/notifications/{id}", saved.getId());
        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testGetNotification_NotFound() {
        UUID randomId = UUID.randomUUID();
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/notifications/{id}", Map.class, randomId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("message").toString()).contains("Notification not found");
    }

    @Test
    void testCreate_ValidationFails() {
        NotificationCreateRequest invalidRequest = new NotificationCreateRequest(null, "", null);

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/notifications", invalidRequest, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        String errorsString = response.getBody().get("errors").toString();

        assertThat(errorsString).contains("message", "must not be blank");
        assertThat(errorsString).contains("userId", "must not be null");
        assertThat(errorsString).contains("type", "must not be null");
    }
}