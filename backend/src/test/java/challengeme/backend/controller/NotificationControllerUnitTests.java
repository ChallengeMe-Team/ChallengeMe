package challengeme.backend.controller;

import challengeme.backend.controller.NotificationController;
import challengeme.backend.exception.GlobalExceptionHandler;
import challengeme.backend.exception.NotificationNotFoundException;
import challengeme.backend.model.Notification;
import challengeme.backend.model.NotificationType;
import challengeme.backend.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FIȘIER FINAL
 * Test unitar pentru Controller (Web Layer) folosind MockMvc.
 * Oprește Spring Security pentru a evita erorile 401/403.
 * Corectat pentru a aștepta un header 'Location' relativ.
 */
@WebMvcTest(controllers = NotificationController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class) // Oprește Spring Security
@Import(GlobalExceptionHandler.class)
class NotificationControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService; // Mock-uim serviciul

    @Test
    void testCreateNotification() throws Exception {
        Notification notification = new Notification(null, UUID.randomUUID(), "Test", NotificationType.SYSTEM, null, false);
        Notification createdNotification = new Notification(UUID.randomUUID(), notification.getUserId(), "Test", NotificationType.SYSTEM, LocalDateTime.now(), false);

        // Mock-uim serviciul
        when(notificationService.createNotification(any(Notification.class))).thenReturn(createdNotification);

        // Facem cererea HTTP simulată
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isCreated()) // Așteptăm 201 Created
                .andExpect(jsonPath("$.id").value(createdNotification.getId().toString()))
                .andExpect(jsonPath("$.message").value("Test"))
                // --- AICI ESTE CORECȚIA FINALĂ ---
                // Așteptăm un URL relativ, nu "http://localhost"
                .andExpect(header().string("Location", "/api/notifications/" + createdNotification.getId()));
    }

    @Test
    void testCreateNotification_ValidationFails() throws Exception {
        // Trimitem un mesaj null, care încalcă @NotBlank
        Notification notification = new Notification(null, UUID.randomUUID(), null, NotificationType.SYSTEM, null, false);

        // Acest test verifică dacă @Valid funcționează și dacă GlobalExceptionHandler prinde eroarea
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isBadRequest()); // Așteptăm 400 Bad Request
    }

    @Test
    void testGetNotificationById_Success() throws Exception {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification(id, UUID.randomUUID(), "Test", NotificationType.SYSTEM, LocalDateTime.now(), false);

        when(notificationService.getNotificationById(id)).thenReturn(notification);

        mockMvc.perform(get("/api/notifications/{id}", id))
                .andExpect(status().isOk()) // Așteptăm 200 OK
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void testGetNotificationById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();

        // Mock-uim serviciul să arunce excepția
        when(notificationService.getNotificationById(id)).thenThrow(new NotificationNotFoundException("Not found"));

        // Verificăm dacă GlobalExceptionHandler prinde excepția și returnează 404
        mockMvc.perform(get("/api/notifications/{id}", id))
                .andExpect(status().isNotFound()); // Așteptăm 404 Not Found
    }

    @Test
    void testDeleteNotification_Success() throws Exception {
        UUID id = UUID.randomUUID();
        // Nu e nevoie de mock pentru 'doNothing' (void)

        mockMvc.perform(delete("/api/notifications/{id}", id))
                .andExpect(status().isNoContent()); // Așteptăm 204 No Content
    }

    @Test
    void testDeleteNotification_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        // Mock-uim serviciul să arunce excepția
        doThrow(new NotificationNotFoundException("Not found")).when(notificationService).deleteNotification(id);

        mockMvc.perform(delete("/api/notifications/{id}", id))
                .andExpect(status().isNotFound()); // Așteptăm 404 Not Found
    }
}