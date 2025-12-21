package challengeme.backend.controller;

import challengeme.backend.dto.UserBadgeDTO;
import challengeme.backend.dto.request.create.UserBadgeCreateRequest;
import challengeme.backend.dto.request.update.UserBadgeUpdateRequest;
import challengeme.backend.mapper.UserBadgeMapper;
import challengeme.backend.model.UserBadge;
import challengeme.backend.service.UserBadgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/userbadges")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserBadgeController {

    private final UserBadgeService service;
    private final UserBadgeMapper mapper;

    @GetMapping
    public ResponseEntity<List<UserBadgeDTO>> getAllUserBadges() {
        List<UserBadgeDTO> badges = service.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBadgeDTO> getUserBadgeById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.findUserBadge(id)));
    }

    @PostMapping
    public ResponseEntity<UserBadgeDTO> createUserBadge(@RequestBody UserBadgeCreateRequest req) {
        UserBadge created = service.createUserBadge(req.getUserId(), req.getBadgeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserBadgeDTO> updateUserBadge(@PathVariable UUID id,
                                                        @RequestBody UserBadgeUpdateRequest req) {
        UserBadge updated = service.updateUserBadge(id, req);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserBadge(@PathVariable UUID id) {
        service.deleteUserBadge(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<UserBadgeDTO>> getBadgesByUser(@PathVariable String username) {
        List<UserBadgeDTO> userBadges = service.getBadgesByUsername(username)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userBadges);
    }
}
