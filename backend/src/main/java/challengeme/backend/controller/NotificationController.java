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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor // Pentru injecție
@CrossOrigin(origins = "*") // Pentru CORS
public class NotificationController {

    private final NotificationService service;
    private final NotificationMapper mapper;

    @PostMapping
    public ResponseEntity<NotificationDTO> create(@Valid @RequestBody NotificationCreateRequest request) {
        Notification saved = service.createNotification(request);
        URI location = URI.create("/api/notifications/" + saved.getId());
        return ResponseEntity.created(location).body(mapper.toDTO(saved));
    }

    @GetMapping
    public List<NotificationDTO> getAll() {
        return service.getAllNotifications().stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/user/{userId}")
    public List<NotificationDTO> getByUser(@PathVariable UUID userId) {
        return service.getNotificationsByUserId(userId).stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.getNotificationById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<NotificationDTO> update(@PathVariable UUID id,
                                                  @RequestBody NotificationUpdateRequest request) {
        Notification updated = service.updateNotification(id, request);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}