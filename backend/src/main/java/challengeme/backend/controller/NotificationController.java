package challengeme.backend.controller;

import challengeme.backend.model.Notification;
import challengeme.backend.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * FIȘIER MODIFICAT MASIV
 * - Calea de bază este acum /api/notifications
 * - S-a adăugat @CrossOrigin(origins = "*")
 * - S-a adăugat @RequiredArgsConstructor (nu mai folosește @Autowired)
 * - Nu mai folosește DTO-uri, lucrează direct cu Modelul
 * - Logica de verificare a fost simplificată (se bazează pe excepțiile din service)
 */
@RestController
@RequestMapping("/api/notifications") // Cale actualizată
@RequiredArgsConstructor // Pentru injecție
@CrossOrigin(origins = "*") // Pentru CORS
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody Notification notification) {
        // Primește direct modelul, nu un DTO
        Notification createdNotification = notificationService.createNotification(notification);

        URI location = ServletUriComponentsBuilder
                .fromPath("/api/notifications/{id}") // Cale actualizată
                .buildAndExpand(createdNotification.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdNotification);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable UUID userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable UUID id) {
        // Logica e mai simplă: serviciul va arunca excepție dacă nu-l găsește
        Notification notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Notification> updateNotificationStatus(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        // Am înlocuit UpdateNotificationDto cu un Map generic pentru update parțial
        Notification updatedNotification = notificationService.updateNotificationStatus(id, updates);
        return ResponseEntity.ok(updatedNotification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        // Logica e mai simplă: serviciul aruncă excepție dacă nu găsește
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}