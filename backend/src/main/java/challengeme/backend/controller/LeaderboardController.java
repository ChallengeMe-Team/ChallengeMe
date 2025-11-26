package challengeme.backend.controller;

import challengeme.backend.dto.LeaderboardDTO;
import challengeme.backend.dto.request.create.LeaderboardCreateRequest;
import challengeme.backend.dto.request.update.LeaderboardUpdateRequest;
import challengeme.backend.mapper.LeaderboardMapper;
import challengeme.backend.model.Leaderboard;
import challengeme.backend.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService service;
    private final LeaderboardMapper mapper;

    @PostMapping
    public ResponseEntity<LeaderboardDTO> create(@RequestBody LeaderboardCreateRequest req) {
        Leaderboard created = service.create(req.getUserId(), req.getTotalPoints());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardDTO>> all() {
        return ResponseEntity.ok(service.getAll().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<LeaderboardDTO>> sorted() {
        return ResponseEntity.ok(service.getSorted().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaderboardDTO> get(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.get(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaderboardDTO> update(@PathVariable UUID id,
                                                 @RequestBody LeaderboardUpdateRequest req) {
        Leaderboard updated = service.update(id, req);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
