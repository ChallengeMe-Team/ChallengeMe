package challengeme.backend.controller;

import challengeme.backend.model.Leaderboard;
import challengeme.backend.service.LeaderboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class LeaderboardController {

    private final LeaderboardService service;

    public LeaderboardController(LeaderboardService service) {
        this.service = service;
    }


    public static class CreateRequest {
        public UUID userId;
        public int totalPoints;
    }
    public static class UpdateRequest {
        public Integer totalPoints;
    }

    @PostMapping
    public ResponseEntity<Leaderboard> create(@RequestBody CreateRequest req) {
        Leaderboard created = service.create(req.userId, req.totalPoints);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Leaderboard>> all() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<Leaderboard>> sorted() {
        return ResponseEntity.ok(service.getSorted());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Leaderboard> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Leaderboard> update(@PathVariable UUID id,
                                              @RequestBody UpdateRequest req) {
        Leaderboard updated = service.update(id, req.totalPoints);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
