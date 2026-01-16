package challengeme.backend.controller;

import challengeme.backend.dto.LeaderboardDTO;
import challengeme.backend.dto.LeaderboardResponseDTO;
import challengeme.backend.dto.request.create.LeaderboardCreateRequest;
import challengeme.backend.dto.request.update.LeaderboardUpdateRequest;
import challengeme.backend.mapper.LeaderboardMapper;
import challengeme.backend.model.Leaderboard;
import challengeme.backend.model.LeaderboardRange;
import challengeme.backend.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing the competitive aspect of the application.
 * It provides endpoints for tracking user rankings, calculating scores,
 * and retrieving leaderboard data filtered by different time ranges.
 */
@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService service;
    private final LeaderboardMapper mapper;

    /**
     * Manually creates a leaderboard entry for a specific user.
     * Primarily used for administrative corrections or initial seeding.
     * @param req DTO containing the user ID and initial total points.
     * @return a ResponseEntity with the created LeaderboardDTO and 201 Created status.
     */
    @PostMapping
    public ResponseEntity<LeaderboardDTO> create(@RequestBody LeaderboardCreateRequest req) {
        Leaderboard created = service.create(req.getUserId(), req.getTotalPoints());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    /**
     * Retrieves the leaderboard data filtered by a specific time range.
     * This is the main endpoint for the competitive UI module.
     * @param range The time period for ranking (e.g., ALL_TIME, WEEKLY, MONTHLY).
     * Defaults to ALL_TIME if not specified.
     * @return a ResponseEntity containing a list of ranked users (LeaderboardResponseDTO).
     */
    @GetMapping
    public ResponseEntity<List<LeaderboardResponseDTO>> getLeaderboard(
            @RequestParam(value = "range", defaultValue = "ALL_TIME") LeaderboardRange range) {
        return ResponseEntity.ok(service.getFilteredLeaderboard(range));
    }

    /**
     * Retrieves a simple sorted list of all leaderboard entries.
     * Useful for raw data analysis or debugging global rankings.
     * @return a ResponseEntity containing a sorted list of LeaderboardDTOs.
     */
    @GetMapping("/sorted")
    public ResponseEntity<List<LeaderboardDTO>> sorted() {
        return ResponseEntity.ok(service.getSorted().stream().map(mapper::toDTO).collect(Collectors.toList()));
    }

    /**
     * Retrieves a specific leaderboard entry by its unique ID.
     * @param id the UUID of the leaderboard record.
     * @return a ResponseEntity containing the found LeaderboardDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LeaderboardDTO> get(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.get(id)));
    }

    /**
     * Updates an existing leaderboard entry, typically used to sync points.
     * @param id the UUID of the leaderboard record to update.
     * @param req DTO containing updated point values.
     * @return a ResponseEntity containing the updated LeaderboardDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LeaderboardDTO> update(@PathVariable UUID id,
                                                 @RequestBody LeaderboardUpdateRequest req) {
        Leaderboard updated = service.update(id, req);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    /**
     * Removes a user's entry from the leaderboard.
     * @param id the UUID of the record to be deleted.
     * @return a 204 No Content status on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
