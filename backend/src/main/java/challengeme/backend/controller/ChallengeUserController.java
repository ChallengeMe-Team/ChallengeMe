package challengeme.backend.controller;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.dto.request.create.ChallengeUserCreateRequest;
import challengeme.backend.dto.request.update.ChallengeUserUpdateRequest;
import challengeme.backend.dto.request.update.UpdateChallengeRequest;
import challengeme.backend.mapper.ChallengeUserMapper;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.service.ChallengeUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing the link between Users and Challenges.
 * It handles the lifecycle of a user's participation in a challenge, including
 * self-starting, assigning challenges to friends, and updating progress statuses.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/challenge-users")
@RequiredArgsConstructor
public class ChallengeUserController {

    private final ChallengeUserService service;
    private final ChallengeUserMapper mapper;

    /**
     * Directly creates a link between a user and a challenge.
     * Primarily used for administrative purposes or internal initialization.
     * * @param request DTO containing user ID and challenge ID.
     * @return a ResponseEntity with the created ChallengeUserDTO.
     */
    @PostMapping
    public ResponseEntity<ChallengeUserDTO> create(@RequestBody ChallengeUserCreateRequest request) {
        ChallengeUser created = service.createChallengeUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    /**
     * Retrieves all user-challenge associations in the system.
     * * @return a list of all ChallengeUser entries.
     */
    @GetMapping
    public ResponseEntity<List<ChallengeUserDTO>> getAll() {
        List<ChallengeUserDTO> dtos = service.getAllChallengeUsers().stream()
                .map(mapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Retrieves a specific user-challenge link by its unique ID.
     * * @param id the UUID of the association entry.
     * @return the found ChallengeUserDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeUserDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.getChallengeUserById(id)));
    }

    /**
     * Retrieves all challenges associated with a specific user.
     * Used to populate the user's dashboard or inbox.
     * * @param userId the UUID of the user.
     * @return a list of challenges the user is involved in.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChallengeUserDTO>> getByUser(@PathVariable UUID userId) {
        List<ChallengeUserDTO> dtos = service.getChallengeUsersByUserId(userId).stream()
                .map(mapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Logic for social interaction: allows a user to assign a challenge to a friend.
     * Triggers notifications and initializes the status as PENDING.
     * * @param request DTO containing target user and challenge details.
     * @return the newly created assignment DTO.
     */
    @PostMapping("/assign")
    public ResponseEntity<ChallengeUserDTO> assignChallenge(@Valid @RequestBody ChallengeUserCreateRequest request) {
        ChallengeUser created = service.assignChallenge(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    /**
     * Partially updates the status of a challenge (e.g., from ACCEPTED to COMPLETED).
     * * @param id the ID of the challenge entry.
     * @param request DTO containing the status update.
     * @return the updated ChallengeUserDTO.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ChallengeUserDTO> updateStatus(@PathVariable UUID id,
                                                         @RequestBody ChallengeUserUpdateRequest request) {
        ChallengeUser updated = service.updateChallengeUserStatus(id, request);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    /**
     * Deletes a user-challenge association. Used for declining or removing challenges.
     * * @param id the unique ID of the association.
     * @return 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteChallengeUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint for a user to formally accept a challenge and sign the "commitment".
     * Automatically extracts the current user's identity from the security context.
     * * @param challengeId the UUID of the challenge being accepted.
     * @param request DTO containing acceptance details like startDate or deadline.
     * @param principal the authenticated user injected by Spring Security.
     * @return the created participation DTO.
     */
    @PostMapping("/{challengeId}/accept")
    public ResponseEntity<ChallengeUserDTO> acceptChallenge(
            @PathVariable UUID challengeId,
            @RequestBody UpdateChallengeRequest request,
            Principal principal) {

        ChallengeUserDTO newChallengeUser = service.acceptChallenge(challengeId, principal.getName(), request);

        return ResponseEntity.ok(newChallengeUser);
    }

    /**
     * Filters a user's challenges based on their current status.
     * Useful for tab-based UI (e.g., "Active", "Completed", "Pending").
     * * @param userId the UUID of the user.
     * @param status the string status to filter by (e.g., 'COMPLETED').
     * @return a filtered list of challenge associations.
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<ChallengeUserDTO>> getChallengeUsersByStatus(
            @PathVariable UUID userId,
            @PathVariable String status) {

        List<ChallengeUserDTO> challenges = service.getChallengeUsersByStatus(userId, status);
        return ResponseEntity.ok(challenges);
    }
}
