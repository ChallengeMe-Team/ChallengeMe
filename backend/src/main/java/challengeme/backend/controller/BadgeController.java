package challengeme.backend.controller;

import challengeme.backend.model.Badge;
import challengeme.backend.service.BadgeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "*")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) { this.badgeService = badgeService; }

    @GetMapping
    public ResponseEntity<List<Badge>> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Badge> getBadgeById(@PathVariable UUID id) {
        return ResponseEntity.ok(badgeService.getBadgeById(id));
    }

    @PostMapping
    public ResponseEntity<Badge> createBadge(@Valid @RequestBody Badge badge) {
        Badge created = badgeService.createBadge(badge);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Badge> updateBadge(@PathVariable UUID id, @Valid @RequestBody Badge badge) {
        Badge updated = badgeService.updateBadge(id, badge);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable UUID id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.noContent().build();
    }

}
