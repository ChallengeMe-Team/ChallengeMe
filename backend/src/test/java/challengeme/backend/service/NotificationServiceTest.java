package challengeme.backend.service;

import challengeme.backend.dto.CreateNotificationDto;
import challengeme.backend.dto.UpdateNotificationDto;
import challengeme.backend.model.Notification;
import challengeme.backend.model.NotificationType;
import challengeme.backend.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Teste unitare pentru NotificationService.
 * (Versiune completa si corectata)
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    // --- TESTE PENTRU METODELE GET ---

    @Test
    void testGetAllNotifications() {
        Notification n1 = new Notification(UUID.randomUUID(), UUID.randomUUID(), "Msg 1", NotificationType.SYSTEM, LocalDateTime.now(), false);
        Notification n2 = new Notification(UUID.randomUUID(), UUID.randomUUID(), "Msg 2", NotificationType.BADGE, LocalDateTime.now(), false);
        when(notificationRepository.findAll()).thenReturn(List.of(n1, n2));

        List<Notification> result = notificationService.getAllNotifications();

        assertEquals(2, result.size());
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void testGetNotificationById_Found() {
        UUID id = UUID.randomUUID();
        Notification n = new Notification(id, UUID.randomUUID(), "Test", NotificationType.SYSTEM, LocalDateTime.now(), false);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(n));

        Optional<Notification> result = notificationService.getNotificationById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(notificationRepository, times(1)).findById(id);
    }

    @Test
    void testGetNotificationById_NotFound() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Notification> result = notificationService.getNotificationById(id);

        assertFalse(result.isPresent());
        verify(notificationRepository, times(1)).findById(id);
    }

    @Test
    void testGetNotificationsByUserId() {
        UUID userId = UUID.randomUUID();
        Notification n1 = new Notification(UUID.randomUUID(), userId, "Msg 1", NotificationType.CHALLENGE, LocalDateTime.now(), false);
        when(notificationRepository.findByUserId(userId)).thenReturn(List.of(n1));

        List<Notification> result = notificationService.getNotificationsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        verify(notificationRepository, times(1)).findByUserId(userId);
    }

    // --- TESTE PENTRU CREATE, UPDATE, DELETE ---

    @Test
    void testCreateNotification() {
        CreateNotificationDto dto = new CreateNotificationDto();
        dto.setUserId(UUID.randomUUID());
        dto.setMessage("Test message");
        dto.setType(NotificationType.CHALLENGE);

        Notification savedNotification = new Notification(
                UUID.randomUUID(),
                dto.getUserId(),
                dto.getMessage(),
                dto.getType(),
                LocalDateTime.now(),
                false
        );

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notificationService.createNotification(dto);

        assertNotNull(result);
        assertEquals(savedNotification.getId(), result.getId());
        assertEquals(dto.getMessage(), result.getMessage());
        assertEquals(dto.getType(), result.getType());
        assertFalse(result.isRead());
        assertNotNull(result.getTimestamp());

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testUpdateNotificationStatus_Success() {
        UUID notificationId = UUID.randomUUID();
        UpdateNotificationDto dto = new UpdateNotificationDto();
        dto.setIsRead(true);

        Notification existingNotification = new Notification(
                notificationId,
                UUID.randomUUID(),
                "Old message",
                NotificationType.BADGE,
                LocalDateTime.now(),
                false
        );

        Notification updatedNotification = new Notification(
                notificationId,
                existingNotification.getUserId(),
                existingNotification.getMessage(),
                existingNotification.getType(),
                LocalDateTime.now(),
                true
        );

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(existingNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(updatedNotification);

        Optional<Notification> result = notificationService.updateNotificationStatus(notificationId, dto);

        assertTrue(result.isPresent());
        assertEquals(notificationId, result.get().getId());
        assertTrue(result.get().isRead());
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, times(1)).save(existingNotification);
    }

    @Test
    void testUpdateNotificationStatus_NotFound() {
        UUID notificationId = UUID.randomUUID();
        UpdateNotificationDto dto = new UpdateNotificationDto();
        dto.setIsRead(true);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        Optional<Notification> result = notificationService.updateNotificationStatus(notificationId, dto);

        assertFalse(result.isPresent());
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void testDeleteNotification_Success() {
        UUID notificationId = UUID.randomUUID();
        when(notificationRepository.existsById(notificationId)).thenReturn(true);

        boolean result = notificationService.deleteNotification(notificationId);

        assertTrue(result);
        verify(notificationRepository, times(1)).existsById(notificationId);
        verify(notificationRepository, times(1)).deleteById(notificationId);
    }

    @Test
    void testDeleteNotification_NotFound() {
        UUID notificationId = UUID.randomUUID();
        when(notificationRepository.existsById(notificationId)).thenReturn(false);

        boolean result = notificationService.deleteNotification(notificationId);

        assertFalse(result);
        verify(notificationRepository, times(1)).existsById(notificationId);
        verify(notificationRepository, never()).deleteById(notificationId);
    }
}