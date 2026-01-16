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

/**
 * Controller responsible for managing challenge definitions and their lifecycle.
 * Handles operations for creating, retrieving, updating, and deleting challenges,
 * as well as updating the participation status of users in specific challenges.
 */
@RestController
@RequestMapping("/api/challenges")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService service;
    private final ChallengeMapper mapper;

    /**
     * Retrieves a list of all available challenges in the system.
     * * @return a list of ChallengeDTOs.
     */
    @GetMapping
    public List<ChallengeDTO> getAll() {
        return service.getAllChallenges()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * Retrieves the details of a specific challenge by its ID.
     * * @param id the unique identifier of the challenge.
     * @return a ResponseEntity containing the ChallengeDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.getChallengeById(id)));
    }

    /**
     * Creates a new challenge definition.
     * * @param request the DTO containing the data for the new challenge.
     * @return a ResponseEntity with the created ChallengeDTO and HTTP 201 status.
     */
    @PostMapping
    public ResponseEntity<ChallengeDTO> create(@Valid @RequestBody ChallengeCreateRequest request) {
        Challenge entity = mapper.toEntity(request);
        Challenge saved = service.addChallenge(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(saved));
    }

    /**
     * Updates an existing challenge definition.
     * * @param id the ID of the challenge to be updated.
     * @param request the DTO containing the updated fields.
     * @return a ResponseEntity containing the updated ChallengeDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChallengeDTO> update(@PathVariable UUID id,
                                               @Valid @RequestBody ChallengeUpdateRequest request) {
        Challenge updated = service.updateChallenge(id, request);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    /**
     * Deletes a challenge definition from the system.
     * * @param id the ID of the challenge to remove.
     * @return a ResponseEntity with HTTP 204 No Content status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all challenges created by a specific user.
     * * @param username the creator's username.
     * @return a list of ChallengeDTOs associated with the given creator.
     */
    @GetMapping("/user/{username}")
    public List<ChallengeDTO> getByUser(@PathVariable String username) {
        return service.getChallengesByCreator(username)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * Updates the progress status of a user for a specific challenge.
     * This endpoint links the user's activity to the challenge definition.
     * * @param id the ID of the challenge link/entry.
     * @param request the DTO containing the new status (e.g., COMPLETED, ACCEPTED).
     * @return a ResponseEntity containing the updated ChallengeUserDTO.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ChallengeUserDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateChallengeRequest request) {

        ChallengeUserDTO updatedChallenge = service.updateStatus(id, request);

        return ResponseEntity.ok(updatedChallenge);
    }
}
