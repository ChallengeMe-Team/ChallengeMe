package challengeme.backend.controller;

import challengeme.backend.model.UserBadge;
import challengeme.backend.service.UserBadgeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class UserBadgeController {

    @Autowired
    private UserBadgeService userBadgeService;

    // GET all
    @GetMapping("/userbadges")
    public List<UserBadge> findAll() {
        return userBadgeService.findAll();
    }

    // GET by id
    @GetMapping("/userbadges/{id}")
    public UserBadge findUserBadge(@PathVariable UUID id) {
        return userBadgeService.findUserBadge(id);
    }

    // POST (create)
    @PostMapping("/userbadges")
    public ResponseEntity<UserBadge> addUserBadge(@Valid @RequestBody UserBadge userBadge) {
        userBadgeService.createUserBadge(userBadge);
        return ResponseEntity.status(HttpStatus.CREATED).body(userBadge);
    }

    // PUT (update)
    @PutMapping("/userbadges/{id}")
    public void updateUserBadge(@PathVariable UUID id, @Valid @RequestBody UserBadge userBadge) {
        userBadgeService.updateUserBadge(id, userBadge);
    }

    // DELETE
    @DeleteMapping("/userbadges/{id}")
    public ResponseEntity<Void> deleteUserBadge(@PathVariable UUID id) {
        userBadgeService.deleteUserBadge(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
