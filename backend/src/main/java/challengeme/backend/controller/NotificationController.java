package challengeme.backend.controller;

import challengeme.backend.dto.CreateNotificationDto;
import challengeme.backend.dto.UpdateNotificationDto;
import challengeme.backend.model.Notification;
import challengeme.backend.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Controller-ul REST.
 * Expune endpoint-urile HTTP pentru operatiile CRUD.
 */
@RestController
@RequestMapping("/api/v1/notifications") // Am pastrat un path neutru
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody CreateNotificationDto dto) {
        Notification createdNotification = notificationService.createNotification(dto);

        // Am înlocuit .fromCurrentRequest() cu .fromPath()
        // pentru a nu mai depinde de contextul cererii HTTP,
        // context care lipsește în testele unitare pure.
        URI location = ServletUriComponentsBuilder
                .fromPath("/api/v1/notifications/{id}") // Construim calea manual
                .buildAndExpand(createdNotification.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdNotification);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable UUID id) {
        return notificationService.getNotificationById(id)
                .map(notification -> ResponseEntity.ok(notification))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Notification> updateNotificationStatus(@PathVariable UUID id, @Valid @RequestBody UpdateNotificationDto dto) {
        return notificationService.updateNotificationStatus(id, dto)
                .map(updatedNotification -> ResponseEntity.ok(updatedNotification))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        if (notificationService.deleteNotification(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}