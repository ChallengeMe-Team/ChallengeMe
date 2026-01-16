package challengeme.backend.controller;

import challengeme.backend.dto.UserBadgeDTO;
import challengeme.backend.dto.request.create.UserBadgeCreateRequest;
import challengeme.backend.dto.request.update.UserBadgeUpdateRequest;
import challengeme.backend.mapper.UserBadgeMapper;
import challengeme.backend.model.UserBadge;
import challengeme.backend.service.UserBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing the earned achievements of users.
 * While BadgeController manages the general definitions, this class handles
 * the specific instances of users unlocking badges, including the award date.
 */
@RestController
@RequestMapping("/api/userbadges")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserBadgeController {

    private final UserBadgeService service;
    private final UserBadgeMapper mapper;

    /**
     * Retrieves all badge-to-user assignments in the system.
     * Useful for global achievement analytics.
     * @return a ResponseEntity containing a list of UserBadgeDTOs.
     */
    @GetMapping
    public ResponseEntity<List<UserBadgeDTO>> getAllUserBadges() {
        List<UserBadgeDTO> badges = service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(badges);
    }

    /**
     * Retrieves the details of a specific achievement record by its ID.
     * @param id the unique UUID of the UserBadge record.
     * @return a ResponseEntity containing the detailed UserBadgeDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserBadgeDTO> getUserBadgeById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.findUserBadge(id)));
    }

    /**
     * Formally awards a badge to a user.
     * Typically called by the system when a user meets certain criteria (e.g., finishing a quest).
     * @param req DTO containing the user ID and the badge ID to be linked.
     * @return a ResponseEntity with the created UserBadgeDTO and HTTP 201 status.
     */
    @PostMapping
    public ResponseEntity<UserBadgeDTO> createUserBadge(@RequestBody UserBadgeCreateRequest req) {
        UserBadge created = service.createUserBadge(req.getUserId(), req.getBadgeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    /**
     * Updates an achievement record.
     * Can be used to adjust award dates or metadata associated with the user's badge.
     * @param id the UUID of the record to be updated.
     * @param req DTO containing the updated information.
     * @return a ResponseEntity with the updated UserBadgeDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserBadgeDTO> updateUserBadge(@PathVariable UUID id,
                                                        @RequestBody UserBadgeUpdateRequest req) {
        UserBadge updated = service.updateUserBadge(id, req);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    /**
     * Revokes a badge from a user by deleting the association record.
     * @param id the UUID of the UserBadge record to remove.
     * @return HTTP 204 No Content on successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserBadge(@PathVariable UUID id) {
        service.deleteUserBadge(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all badges earned by a specific user identified by their username.
     * Directly feeds the "Achievements" or "Trophy Case" section in the user profile.
     * @param username the identifier of the user.
     * @return a ResponseEntity containing the list of badges earned by the user.
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<List<UserBadgeDTO>> getBadgesByUser(@PathVariable String username) {
        List<UserBadgeDTO> userBadges = service.getBadgesByUsername(username)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userBadges);
    }
}
