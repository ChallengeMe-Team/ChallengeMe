package challengeme.backend.controller;


import challengeme.backend.model.Leaderboard;
import challengeme.backend.service.LeaderboardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private record CreateRequest(UUID userId, int totalPoints) {}
    private record UpdateRequest(Integer totalPoints) {}

    private final LeaderboardService service;

    public LeaderboardController(LeaderboardService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Leaderboard create(@RequestBody CreateRequest req) {
        return service.create(req.userId(), req.totalPoints());
    }

    @GetMapping public List<Leaderboard> all() { return service.getAll(); }

    @GetMapping("/sorted") public List<Leaderboard> sorted() { return service.getSortedDescByPoints(); }

    @GetMapping("/{id}") public Leaderboard get(@PathVariable UUID id) { return service.get(id); }

    @PutMapping("/{id}")
    public Leaderboard update(@PathVariable UUID id, @RequestBody UpdateRequest req) {
        return service.update(id, req.totalPoints());
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
