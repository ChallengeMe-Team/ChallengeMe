package challengeme.backend.controller;

import challengeme.backend.domain.Challenge;
import challengeme.backend.exceptions.ChallengeNotFoundException;
import challengeme.backend.service.ChallengeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        List<Challenge> challenges = service.getAllChallenges();
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChallengeById(@PathVariable UUID id) {
        try {
            Challenge challenge = service.getChallengeById(id);
            return ResponseEntity.ok(challenge);
        } catch (ChallengeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createChallenge(@Valid @RequestBody Challenge challenge) {
        try {
            Challenge created = service.addChallenge(challenge);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateChallenge(@PathVariable UUID id, @Valid @RequestBody Challenge updated) {
        try {
            Challenge saved = service.updateChallenge(id, updated);
            return ResponseEntity.ok(saved);
        } catch (ChallengeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChallenge(@PathVariable UUID id) {
        try {
            service.deleteChallenge(id);
            return ResponseEntity.noContent().build();
        } catch (ChallengeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Clasa internă pentru răspunsurile de eroare
    public record ErrorResponse(String message, LocalDateTime timestamp) {
        public ErrorResponse(String message) {
            this(message, LocalDateTime.now());
        }
    }
}