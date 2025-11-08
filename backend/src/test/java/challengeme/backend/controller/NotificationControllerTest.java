package challengeme.backend.controller;

import challengeme.backend.dto.CreateNotificationDto;
import challengeme.backend.dto.UpdateNotificationDto;
import challengeme.backend.model.Notification;
import challengeme.backend.model.NotificationType;
import challengeme.backend.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

// Folosim DOAR Mockito, FĂRĂ Spring
@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    // Mock-uim serviciul de care are nevoie controller-ul
    @Mock
    private NotificationService notificationService;

    // Injectăm mock-ul de mai sus în controller
    @InjectMocks
    private NotificationController notificationController;

    private Notification notification;
    private UUID notificationId;

    @BeforeEach
    void setUp() {
        notificationId = UUID.randomUUID();
        notification = new Notification(notificationId, UUID.randomUUID(), "Test", NotificationType.SYSTEM, LocalDateTime.now(), false);
    }

    @Test
    void testCreateNotification() {
        CreateNotificationDto dto = new CreateNotificationDto();
        dto.setMessage("Test");

        // Definim ce face mock-ul
        when(notificationService.createNotification(any(CreateNotificationDto.class))).thenReturn(notification);

        // Apelăm metoda direct
        ResponseEntity<Notification> response = notificationController.createNotification(dto);

        // Verificăm răspunsul
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(notificationId);
    }

    // NOTĂ: Testul pentru eșecul validării (@Valid) nu mai poate fi făcut
    // în acest mod, deoarece validarea este o funcționalitate Spring
    // care se întâmplă ÎNAINTE ca metoda controller-ului să fie apelată.

    @Test
    void testGetAllNotifications() {
        // Definim ce face mock-ul
        when(notificationService.getAllNotifications()).thenReturn(List.of(notification));

        // Apelăm metoda direct
        ResponseEntity<List<Notification>> response = notificationController.getAllNotifications();

        // Verificăm răspunsul
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getMessage()).isEqualTo("Test");
    }

    @Test
    void testGetNotificationById_Success() {
        // Definim ce face mock-ul
        when(notificationService.getNotificationById(notificationId)).thenReturn(Optional.of(notification));

        // Apelăm metoda direct
        ResponseEntity<Notification> response = notificationController.getNotificationById(notificationId);

        // Verificăm răspunsul
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(notification);
    }

    @Test
    void testGetNotificationById_NotFound() {
        // Definim ce face mock-ul
        when(notificationService.getNotificationById(notificationId)).thenReturn(Optional.empty());

        // Apelăm metoda direct
        ResponseEntity<Notification> response = notificationController.getNotificationById(notificationId);

        // Verificăm răspunsul
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testUpdateNotificationStatus_Success() {
        UpdateNotificationDto dto = new UpdateNotificationDto();
        dto.setIsRead(true);

        Notification updatedNotification = new Notification(notificationId, UUID.randomUUID(), "Test", NotificationType.SYSTEM, LocalDateTime.now(), true);

        // Definim ce face mock-ul
        when(notificationService.updateNotificationStatus(eq(notificationId), any(UpdateNotificationDto.class)))
                .thenReturn(Optional.of(updatedNotification));

        // Apelăm metoda direct
        ResponseEntity<Notification> response = notificationController.updateNotificationStatus(notificationId, dto);

        // Verificăm răspunsul
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isRead()).isTrue();
    }

    @Test
    void testUpdateNotificationStatus_NotFound() {
        UpdateNotificationDto dto = new UpdateNotificationDto();
        dto.setIsRead(true);

        // Definim ce face mock-ul
        when(notificationService.updateNotificationStatus(eq(notificationId), any(UpdateNotificationDto.class)))
                .thenReturn(Optional.empty());

        // Apelăm metoda direct
        ResponseEntity<Notification> response = notificationController.updateNotificationStatus(notificationId, dto);

        // Verificăm răspunsul
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testDeleteNotification_Success() {
        // Definim ce face mock-ul
        when(notificationService.deleteNotification(notificationId)).thenReturn(true);

        // Apelăm metoda direct
        ResponseEntity<Void> response = notificationController.deleteNotification(notificationId);

        // Verificăm răspunsul
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testDeleteNotification_NotFound() {
        // Definim ce face mock-ul
        when(notificationService.deleteNotification(notificationId)).thenReturn(false);

        // Apelăm metoda direct
        ResponseEntity<Void> response = notificationController.deleteNotification(notificationId);

        // Verificăm răspunsul
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}