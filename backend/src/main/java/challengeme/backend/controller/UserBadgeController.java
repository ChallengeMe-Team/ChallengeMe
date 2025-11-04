package challengeme.backend.controller;

import challengeme.backend.model.UserBadge;
import challengeme.backend.service.UserBadgeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/userbadges")
@CrossOrigin(origins = "*") // permite accesul frontend-ului Angular
public class UserBadgeController {

    private final UserBadgeService userBadgeService;

    public UserBadgeController(UserBadgeService userBadgeService) {
        this.userBadgeService = userBadgeService;
    }

    // GET all
    @GetMapping
    public ResponseEntity<List<UserBadge>> getAllUserBadges() {
        List<UserBadge> badges = userBadgeService.findAll();
        return ResponseEntity.ok(badges);
    }

    // GET by id
    @GetMapping("/{id}")
    public ResponseEntity<UserBadge> getUserBadgeById(@PathVariable UUID id) {
        UserBadge userBadge = userBadgeService.findUserBadge(id);
        return ResponseEntity.ok(userBadge);
    }

    // POST (create)
    @PostMapping
    public ResponseEntity<UserBadge> createUserBadge(@Valid @RequestBody UserBadge userBadge) {
        UserBadge created = userBadgeService.createUserBadge(userBadge);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT (update)
    @PutMapping("/{id}")
    public ResponseEntity<UserBadge> updateUserBadge(@PathVariable UUID id, @Valid @RequestBody UserBadge userBadge) {
        UserBadge updated = userBadgeService.updateUserBadge(id, userBadge);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserBadge(@PathVariable UUID id) {
        userBadgeService.deleteUserBadge(id);
        return ResponseEntity.noContent().build();
    }
}
