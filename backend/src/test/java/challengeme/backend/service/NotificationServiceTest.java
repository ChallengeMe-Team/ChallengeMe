package challengeme.backend.service;

import challengeme.backend.exception.NotificationNotFoundException;
import challengeme.backend.model.Notification;
import challengeme.backend.model.NotificationType;
import challengeme.backend.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FIȘIER MODIFICAT
 * - Testează clasa concretă NotificationService (nu interfața).
 * - Verifică dacă aruncă NotificationNotFoundException.
 * - Lucrează direct cu modelul, fără DTO-uri.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService; // Clasa concretă

    @Test
    void testCreateNotification() {
        Notification notification = new Notification();
        notification.setUserId(UUID.randomUUID());
        notification.setMessage("Test");
        notification.setType(NotificationType.SYSTEM);

        // Când se apelează save, returnăm argumentul primit (care va fi modificat)
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Notification created = notificationService.createNotification(notification);

        // Verificăm dacă serviciul a setat valorile default
        assertNotNull(created.getId());
        assertNotNull(created.getTimestamp());
        assertFalse(created.isRead());
        assertEquals("Test", created.getMessage());

        // Verificăm că 'save' a fost apelat
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testGetNotificationById_Success() {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification(id, UUID.randomUUID(), "Test", NotificationType.SYSTEM, null, false);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

        Notification found = notificationService.getNotificationById(id);

        assertNotNull(found);
        assertEquals(id, found.getId());
    }

    @Test
    void testGetNotificationById_NotFound() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        // Verificăm dacă aruncă excepția corectă
        assertThrows(NotificationNotFoundException.class, () -> {
            notificationService.getNotificationById(id);
        });
    }

    @Test
    void testUpdateNotificationStatus() {
        UUID id = UUID.randomUUID();
        // Notificarea inițială are isRead=false
        Notification notification = new Notification(id, UUID.randomUUID(), "Test", NotificationType.SYSTEM, null, false);

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> updates = new HashMap<>();
        updates.put("isRead", true);

        Notification updated = notificationService.updateNotificationStatus(id, updates);

        // Verificăm că starea s-a actualizat
        assertTrue(updated.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testDeleteNotification_Success() {
        UUID id = UUID.randomUUID();
        // Simulează că notificarea există
        when(notificationRepository.existsById(id)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(id);

        // Executăm metoda
        assertDoesNotThrow(() -> {
            notificationService.deleteNotification(id);
        });

        // Verificăm dacă a fost apelată ștergerea
        verify(notificationRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteNotification_NotFound() {
        UUID id = UUID.randomUUID();
        // Simulează că notificarea NU există
        when(notificationRepository.existsById(id)).thenReturn(false);

        // Verificăm dacă aruncă excepția
        assertThrows(NotificationNotFoundException.class, () -> {
            notificationService.deleteNotification(id);
        });

        // Verificăm că ștergerea NU a fost apelată
        verify(notificationRepository, never()).deleteById(id);
    }
}