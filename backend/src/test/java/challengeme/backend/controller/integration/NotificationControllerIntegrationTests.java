package challengeme.backend.controller.integration;

import challengeme.backend.model.Notification;
import challengeme.backend.model.NotificationType;
import challengeme.backend.repository.NotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integrare care pornește întreaga aplicație (@SpringBootTest).
 * Folosește TestRestTemplate pentru a face cereri HTTP reale.
 * Folosește repository-ul in-memory real.
 * Include autentificare Basic Auth pentru a trece de Spring Security.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=test"
)
class NotificationControllerIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NotificationRepository notificationRepository; // Injectăm repo-ul real

    @AfterEach
    void tearDown() {
        // CRUCIAL: Curățăm repository-ul in-memory după fiecare test
        notificationRepository.findAll().forEach(n -> notificationRepository.deleteById(n.getId()));
    }

    @Test
    void testCreateAndGetNotification() {
        UUID userId = UUID.randomUUID();
        Notification notification = new Notification(null, userId, "Integration Test", NotificationType.CHALLENGE, null, false);

        // 1. Creare (POST)
        ResponseEntity<Notification> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass") // Adaugă autentificare
                .postForEntity(
                        "/api/notifications",
                        notification,
                        Notification.class
                );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());
        assertEquals("Integration Test", createResponse.getBody().getMessage());
        assertFalse(createResponse.getBody().isRead()); // Verificăm default-ul setat de service

        UUID createdId = createResponse.getBody().getId();

        // 2. Obținere (GET)
        ResponseEntity<Notification> getResponse = restTemplate
                .withBasicAuth("testuser", "testpass") // Adaugă autentificare
                .getForEntity(
                        "/api/notifications/{id}",
                        Notification.class,
                        createdId
                );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(createdId, getResponse.getBody().getId());
    }

    @Test
    void testGetNotification_NotFound() {
        UUID randomId = UUID.randomUUID();
        ResponseEntity<Map> getResponse = restTemplate
                .withBasicAuth("testuser", "testpass") // Adaugă autentificare
                .getForEntity(
                        "/api/notifications/{id}",
                        Map.class, // Primim eroarea ca Map
                        randomId
                );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        // Verificăm mesajul de eroare din GlobalExceptionHandler
        assertTrue(getResponse.getBody().get("message").toString().contains("Notification not found"));
    }

    @Test
    void testCreate_ValidationFails() {
        // Trimitem un tip null, care încalcă @NotNull
        Notification notification = new Notification(null, UUID.randomUUID(), "Test", null, null, false);

        ResponseEntity<Map> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass") // Adaugă autentificare
                .postForEntity(
                        "/api/notifications",
                        notification,
                        Map.class // Primim eroarea ca Map
                );

        assertEquals(HttpStatus.BAD_REQUEST, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody().get("errors"));
        // Verificăm eroarea specifică de validare
        assertTrue(createResponse.getBody().get("errors").toString().contains("NotificationType cannot be null"));
    }
}