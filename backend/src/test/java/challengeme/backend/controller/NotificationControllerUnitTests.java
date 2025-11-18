package challengeme.backend.controller;

import challengeme.backend.dto.NotificationDTO;
import challengeme.backend.dto.request.create.NotificationCreateRequest;
import challengeme.backend.dto.request.update.NotificationUpdateRequest;
import challengeme.backend.exception.GlobalExceptionHandler;
import challengeme.backend.exception.NotificationNotFoundException;
import challengeme.backend.mapper.NotificationMapper;
import challengeme.backend.model.Notification;
import challengeme.backend.model.NotificationType;
import challengeme.backend.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Unit tests cu MockMvc – testează controller-ul izolat, folosind mock pentru NotificationService.

@WebMvcTest(controllers = NotificationController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import({GlobalExceptionHandler.class, NotificationControllerUnitTests.NotificationTestConfiguration.class})
class NotificationControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationService service;

    @Autowired
    private NotificationMapper mapper;

    @TestConfiguration
    static class NotificationTestConfiguration {

        @Bean
        public NotificationService notificationService() {
            return mock(NotificationService.class);
        }

        @Bean
        public NotificationMapper notificationMapper() {
            return mock(NotificationMapper.class);
        }
    }

    @Test
    void testCreateNotification_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        NotificationCreateRequest request = new NotificationCreateRequest(userId, "Test message", NotificationType.SYSTEM);
        Notification saved = new Notification(UUID.randomUUID(), userId, "Test message", NotificationType.SYSTEM, LocalDateTime.now(), false);
        NotificationDTO dto = new NotificationDTO(saved.getId(), saved.getUserId(), saved.getMessage(), saved.getType(), saved.getTimestamp(), saved.isRead());

        when(service.createNotification(ArgumentMatchers.any(NotificationCreateRequest.class))).thenReturn(saved);
        when(mapper.toDTO(saved)).thenReturn(dto);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(header().string("Location", "/api/notifications/" + saved.getId()));
    }

    @Test
    void testCreateNotification_ValidationFails() throws Exception {
        NotificationCreateRequest request = new NotificationCreateRequest(null, "", null);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetNotificationById_Success() throws Exception {
        UUID id = UUID.randomUUID();
        Notification n = new Notification(id, UUID.randomUUID(), "Test", NotificationType.SYSTEM, LocalDateTime.now(), false);
        NotificationDTO dto = new NotificationDTO(id, n.getUserId(), n.getMessage(), n.getType(), n.getTimestamp(), n.isRead());

        when(service.getNotificationById(id)).thenReturn(n);
        when(mapper.toDTO(n)).thenReturn(dto);

        mockMvc.perform(get("/api/notifications/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void testGetNotificationById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getNotificationById(id)).thenThrow(new NotificationNotFoundException("Not found"));

        mockMvc.perform(get("/api/notifications/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateNotification_Success() throws Exception {
        UUID id = UUID.randomUUID();
        NotificationUpdateRequest request = new NotificationUpdateRequest(true);

        Notification existing = new Notification(id, UUID.randomUUID(), "Test", NotificationType.SYSTEM, LocalDateTime.now(), false);

        Notification updated = new Notification(id, existing.getUserId(), existing.getMessage(), existing.getType(), existing.getTimestamp(), true);

        NotificationDTO dto = new NotificationDTO(id, updated.getUserId(), updated.getMessage(), updated.getType(), updated.getTimestamp(), updated.isRead());

        when(service.updateNotification(eq(id), ArgumentMatchers.any(NotificationUpdateRequest.class))).thenReturn(updated);
        when(mapper.toDTO(updated)).thenReturn(dto);

        doAnswer(invocation -> {
            NotificationUpdateRequest req = invocation.getArgument(0);
            Notification n = invocation.getArgument(1);
            n.setRead(req.isRead());
            return null;
        }).when(mapper).updateEntity(request, existing);

        mockMvc.perform(patch("/api/notifications/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRead").value(true))
                .andExpect(jsonPath("$.message").value("Test"));
    }

    @Test
    void testDeleteNotification_Success() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/notifications/{id}", id))
                .andExpect(status().isNoContent());
        verify(service, times(1)).deleteNotification(id);
    }

    @Test
    void testDeleteNotification_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new NotificationNotFoundException("Not found")).when(service).deleteNotification(id);

        mockMvc.perform(delete("/api/notifications/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllNotifications() throws Exception {
        Notification n1 = new Notification(UUID.randomUUID(), UUID.randomUUID(), "Msg1", NotificationType.SYSTEM, LocalDateTime.now(), false);
        Notification n2 = new Notification(UUID.randomUUID(), UUID.randomUUID(), "Msg2", NotificationType.CHALLENGE, LocalDateTime.now(), true);
        NotificationDTO dto1 = new NotificationDTO(n1.getId(), n1.getUserId(), n1.getMessage(), n1.getType(), n1.getTimestamp(), n1.isRead());
        NotificationDTO dto2 = new NotificationDTO(n2.getId(), n2.getUserId(), n2.getMessage(), n2.getType(), n2.getTimestamp(), n2.isRead());

        when(service.getAllNotifications()).thenReturn(List.of(n1, n2));
        when(mapper.toDTO(n1)).thenReturn(dto1);
        when(mapper.toDTO(n2)).thenReturn(dto2);

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}