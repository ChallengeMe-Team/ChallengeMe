package challengeme.backend.service;

import challengeme.backend.dto.request.create.NotificationCreateRequest;
import challengeme.backend.dto.request.update.NotificationUpdateRequest;
import challengeme.backend.exception.NotificationNotFoundException;
import challengeme.backend.mapper.NotificationMapper;
import challengeme.backend.model.Notification;
import challengeme.backend.model.NotificationType;
import challengeme.backend.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    private UUID userId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
    }

    // --- CREATE ---

    @Test
    void testCreateNotification_Success() {
        NotificationCreateRequest request = new NotificationCreateRequest(userId, "Hello", NotificationType.SYSTEM);

        Notification entity = new Notification();
        entity.setUserId(userId);
        entity.setMessage("Hello");
        entity.setType(NotificationType.SYSTEM);

        when(notificationMapper.toEntity(request)).thenReturn(entity);
        when(notificationRepository.save(entity)).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(UUID.randomUUID());
            n.setTimestamp(LocalDateTime.now());
            return n;
        });

        Notification result = notificationService.createNotification(request);

        assertNotNull(result.getId());
        assertNotNull(result.getTimestamp());
        assertEquals("Hello", result.getMessage());
        assertFalse(result.isRead());

        verify(notificationMapper, times(1)).toEntity(request);
        verify(notificationRepository, times(1)).save(entity);
    }

    // --- READ ---

    @Test
    void testGetNotificationById_Success() {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification(id, userId, "Test", NotificationType.SYSTEM, LocalDateTime.now(), false);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

        Notification result = notificationService.getNotificationById(id);

        assertEquals(notification, result);
        verify(notificationRepository, times(1)).findById(id);
    }

    @Test
    void testGetNotificationById_NotFound() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationService.getNotificationById(id));
        verify(notificationRepository, times(1)).findById(id);
    }

    @Test
    void testGetAllNotifications() {
        Notification n1 = new Notification(UUID.randomUUID(), userId, "A", NotificationType.SYSTEM, LocalDateTime.now(), false);
        Notification n2 = new Notification(UUID.randomUUID(), userId, "B", NotificationType.SYSTEM, LocalDateTime.now(), false);

        when(notificationRepository.findAll()).thenReturn(List.of(n1, n2));

        List<Notification> results = notificationService.getAllNotifications();

        assertEquals(2, results.size());
        assertTrue(results.contains(n1));
        assertTrue(results.contains(n2));
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void testGetNotificationsByUserId() {
        Notification n1 = new Notification(UUID.randomUUID(), userId, "A", NotificationType.SYSTEM, LocalDateTime.now(), false);

        when(notificationRepository.findByUserId(userId)).thenReturn(List.of(n1));

        List<Notification> results = notificationService.getNotificationsByUserId(userId);

        assertEquals(1, results.size());
        assertEquals(userId, results.get(0).getUserId());
        verify(notificationRepository, times(1)).findByUserId(userId);
    }

    // --- UPDATE ---

    @Test
    void testUpdateNotification_Success() {
        UUID id = UUID.randomUUID();
        Notification existing = new Notification(id, userId, "Old", NotificationType.SYSTEM, LocalDateTime.now(), false);

        NotificationUpdateRequest request = new NotificationUpdateRequest(true);

        when(notificationRepository.findById(id)).thenReturn(Optional.of(existing));

        doAnswer(invocation -> {
            NotificationUpdateRequest req = invocation.getArgument(0);
            Notification n = invocation.getArgument(1);

            n.setRead(req.isRead());
            n.setMessage("Updated");

            return null;
        }).when(notificationMapper).updateEntity(request, existing);

        when(notificationRepository.save(existing)).thenReturn(existing);

        Notification updated = notificationService.updateNotification(id, request);

        assertEquals("Updated", updated.getMessage());
        assertTrue(updated.isRead());

        verify(notificationRepository, times(1)).findById(id);
        verify(notificationMapper, times(1)).updateEntity(request, existing);
        verify(notificationRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateNotification_NotFound() {
        UUID id = UUID.randomUUID();
        NotificationUpdateRequest request = new NotificationUpdateRequest(false);

        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationService.updateNotification(id, request));
        verify(notificationRepository, times(1)).findById(id);
        verify(notificationMapper, never()).updateEntity(any(), any());
        verify(notificationRepository, never()).save(any());
    }

    // --- DELETE ---

    @Test
    void testDeleteNotification_Success() {
        UUID id = UUID.randomUUID();
        Notification existing = new Notification(id, userId, "Test", NotificationType.SYSTEM, LocalDateTime.now(), false);

        when(notificationRepository.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(notificationRepository).delete(existing);

        assertDoesNotThrow(() -> notificationService.deleteNotification(id));

        verify(notificationRepository, times(1)).findById(id);
        verify(notificationRepository, times(1)).delete(existing);
    }

    @Test
    void testDeleteNotification_NotFound() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationService.deleteNotification(id));
        verify(notificationRepository, times(1)).findById(id);
        verify(notificationRepository, never()).delete(any());
    }
}