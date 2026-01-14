package challengeme.backend.controller;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.dto.request.create.ChallengeCreateRequest;
import challengeme.backend.dto.ChallengeDTO;
import challengeme.backend.dto.request.update.ChallengeUpdateRequest;
import challengeme.backend.dto.request.update.UpdateChallengeRequest;
import challengeme.backend.mapper.ChallengeMapper;
import challengeme.backend.model.Challenge;
import challengeme.backend.service.ChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/challenges")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService service;
    private final ChallengeMapper mapper;

    @GetMapping
    public List<ChallengeDTO> getAll() {
        return service.getAllChallenges()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.getChallengeById(id)));
    }

    @PostMapping
    public ResponseEntity<ChallengeDTO> create(@Valid @RequestBody ChallengeCreateRequest request) {
        Challenge entity = mapper.toEntity(request);
        Challenge saved = service.addChallenge(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChallengeDTO> update(@PathVariable UUID id,
                                               @Valid @RequestBody ChallengeUpdateRequest request) {
        Challenge updated = service.updateChallenge(id, request);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{username}")
    public List<ChallengeDTO> getByUser(@PathVariable String username) {
        return service.getChallengesByCreator(username)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ChallengeUserDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateChallengeRequest request) {

        ChallengeUserDTO updatedChallenge = service.updateStatus(id, request);

        return ResponseEntity.ok(updatedChallenge);
    }
}
