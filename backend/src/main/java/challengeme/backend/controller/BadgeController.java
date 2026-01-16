package challengeme.backend.controller;

import challengeme.backend.dto.BadgeDTO;
import challengeme.backend.dto.request.create.BadgeCreateRequest;
import challengeme.backend.dto.request.update.BadgeUpdateRequest;
import challengeme.backend.mapper.BadgeMapper;
import challengeme.backend.model.Badge;
import challengeme.backend.service.BadgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsible for managing badge-related operations.
 * Provides endpoints for retrieving available badges, tracking user achievements,
 * and performing CRUD operations on badge definitions.
 */
@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService service;
    private final BadgeMapper mapper;

    /**
     * Retrieves all available badges in the system.
     * * @return a list of BadgeDTOs representing the global badge catalog.
     */
    @GetMapping
    public List<BadgeDTO> getAll() {
        return service.getAllBadges()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * Retrieves a specific badge by its unique identifier.
     * * @param id the UUID of the badge to be retrieved.
     * @return a ResponseEntity containing the BadgeDTO if found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BadgeDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.getBadgeById(id)));
    }


    /**
     * Retrieves the collection of badges earned by a specific user.
     * Note: Returns the entity directly for simplicity in the current implementation.
     * * @param username the unique username of the user.
     * @return a ResponseEntity containing the list of Badge entities belonging to the user.
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Badge>> getUserBadges(@PathVariable String username) {
        return ResponseEntity.ok(service.getUserBadges(username));
    }

    /**
     * Creates a new badge definition in the system.
     * * @param request the request body containing badge details (name, criteria, icon, etc.).
     * @return a ResponseEntity containing the created BadgeDTO and 201 Created status.
     */
    @PostMapping
    public ResponseEntity<BadgeDTO> create(@Valid @RequestBody BadgeCreateRequest request) {
        Badge entity = mapper.toEntity(request);
        Badge saved = service.createBadge(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(saved));
    }

    /**
     * Updates an existing badge definition.
     * * @param id the UUID of the badge to update.
     * @param request the request body with updated badge information.
     * @return a ResponseEntity containing the updated BadgeDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BadgeDTO> update(@PathVariable UUID id,
                                           @Valid @RequestBody BadgeUpdateRequest request) {
        Badge updated = service.updateBadge(id, request);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    /**
     * Removes a badge definition from the system.
     * * @param id the UUID of the badge to be deleted.
     * @return a ResponseEntity with 204 No Content status on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteBadge(id);
        return ResponseEntity.noContent().build();
    }

}
