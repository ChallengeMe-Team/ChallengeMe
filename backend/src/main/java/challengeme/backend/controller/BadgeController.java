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

@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService service;
    private final BadgeMapper mapper;

    @GetMapping
    public List<BadgeDTO> getAll() {
        return service.getAllBadges()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BadgeDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.getBadgeById(id)));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Badge>> getUserBadges(@PathVariable String username) {
        // Returnăm direct entitatea pentru simplitate în acest task
        return ResponseEntity.ok(service.getUserBadges(username));
    }

    @PostMapping
    public ResponseEntity<BadgeDTO> create(@Valid @RequestBody BadgeCreateRequest request) {
        Badge entity = mapper.toEntity(request);
        Badge saved = service.createBadge(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BadgeDTO> update(@PathVariable UUID id,
                                           @Valid @RequestBody BadgeUpdateRequest request) {
        Badge updated = service.updateBadge(id, request);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteBadge(id);
        return ResponseEntity.noContent().build();
    }

}
