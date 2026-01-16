package challengeme.backend.controller;

import challengeme.backend.dto.NotificationDTO;
import challengeme.backend.dto.request.create.NotificationCreateRequest;
import challengeme.backend.dto.request.update.NotificationUpdateRequest;
import challengeme.backend.mapper.NotificationMapper;
import challengeme.backend.model.Notification;
import challengeme.backend.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;


/**
 * Controller responsible for managing user notifications.
 * Handles the delivery of system alerts, challenge assignments, and social updates.
 * Provides functionality to retrieve, update status (read/unread), and batch process notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService service;
    private final NotificationMapper mapper;

    /**
     * Creates a new notification in the system.
     * Often triggered by background events such as a friend assigning a challenge.
     * @param request DTO containing target user ID, message, and notification type.
     * @return a ResponseEntity with the created NotificationDTO and the URI location of the resource.
     */
    @PostMapping
    public ResponseEntity<NotificationDTO> create(@Valid @RequestBody NotificationCreateRequest request) {
        Notification saved = service.createNotification(request);
        URI location = URI.create("/api/notifications/" + saved.getId());
        return ResponseEntity.created(location).body(mapper.toDTO(saved));
    }

    /**
     * Retrieves all notifications stored in the system.
     * Primarily used for administrative oversight.
     * @return a list of all NotificationDTOs.
     */
    @GetMapping
    public List<NotificationDTO> getAll() {
        return service.getAllNotifications().stream().map(mapper::toDTO).toList();
    }

    /**
     * Retrieves the notification inbox for a specific user.
     * Used by the frontend to populate the notification dropdown or dedicated alerts page.
     * @param userId the UUID of the user whose notifications are being requested.
     * @return a list of NotificationDTOs belonging to the specified user.
     */
    @GetMapping("/user/{userId}")
    public List<NotificationDTO> getByUser(@PathVariable UUID userId) {
        return service.getNotificationsByUserId(userId).stream().map(mapper::toDTO).toList();
    }

    /**
     * Retrieves detailed information about a single notification.
     * @param id the unique UUID of the notification.
     * @return a ResponseEntity containing the found NotificationDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.getNotificationById(id)));
    }

    /**
     * Partially updates a notification (e.g., marking it as read).
     * @param id the UUID of the notification to update.
     * @param request DTO containing the update fields (isRead status).
     * @return a ResponseEntity with the updated NotificationDTO.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<NotificationDTO> update(@PathVariable UUID id,
                                                  @RequestBody NotificationUpdateRequest request) {
        Notification updated = service.updateNotification(id, request);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    /**
     * Removes a notification from the system.
     * @param id the UUID of the notification to be deleted.
     * @return a 204 No Content status on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Bulk operation to mark all notifications for a specific user as read.
     * Optimizes user experience by allowing a single click to clear the inbox.
     * @param userId the UUID of the user.
     * @return a 200 OK status on success.
     */
    @PostMapping("/user/{userId}/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@PathVariable UUID userId) {
        service.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }
}