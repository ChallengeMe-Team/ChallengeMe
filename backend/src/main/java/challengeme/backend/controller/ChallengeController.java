package challengeme.backend.controller;

import challengeme.backend.domain.Challenge;
import challengeme.backend.service.ChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService service;

    public ChallengeController(ChallengeService service) {
        this.service = service;
    }
    @GetMapping
    public List<Challenge> getAllChallenges() {
        return service.getAllChallenges();
    }

    @GetMapping("/{id}")
    public Challenge getChallengeById(@PathVariable UUID id) {
        return service.getChallengeById(id);
    }

    @PostMapping
    public ResponseEntity<Challenge> createChallenge(@RequestBody Challenge challenge) {
        Challenge created = service.addChallenge(challenge);
        return ResponseEntity.ok(created);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Challenge> updateChallenge(@PathVariable UUID id, @RequestBody Challenge updated) {
        try {
            Challenge saved = service.updateChallenge(id, updated);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable UUID id) {
        service.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }

}
