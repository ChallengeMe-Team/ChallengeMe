package challengeme.backend.controller;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.dto.request.create.ChallengeUserCreateRequest;
import challengeme.backend.dto.request.update.ChallengeUserUpdateRequest;
import challengeme.backend.dto.request.update.UpdateChallengeRequest;
import challengeme.backend.mapper.ChallengeUserMapper;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.service.ChallengeUserService;
import challengeme.backend.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user-challenges")
@RequiredArgsConstructor
public class ChallengeUserController {

    private final ChallengeUserService service;
    private final ChallengeUserMapper mapper;

    @PostMapping
    public ResponseEntity<ChallengeUserDTO> create(@RequestBody ChallengeUserCreateRequest request) {
        ChallengeUser created = service.createChallengeUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    @GetMapping
    public ResponseEntity<List<ChallengeUserDTO>> getAll() {
        List<ChallengeUserDTO> dtos = service.getAllChallengeUsers().stream()
                .map(mapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeUserDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(service.getChallengeUserById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChallengeUserDTO>> getByUser(@PathVariable UUID userId) {
        List<ChallengeUserDTO> dtos = service.getChallengeUsersByUserId(userId).stream()
                .map(mapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/assign")
    public ResponseEntity<ChallengeUserDTO> assignChallenge(@Valid @RequestBody ChallengeUserCreateRequest request) {
        ChallengeUser created = service.assignChallenge(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ChallengeUserDTO> updateStatus(@PathVariable UUID id,
                                                         @RequestBody ChallengeUserUpdateRequest request) {
        ChallengeUser updated = service.updateChallengeUserStatus(id, request);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteChallengeUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{challengeId}/accept")
    public ResponseEntity<ChallengeUserDTO> acceptChallenge(
            @PathVariable UUID challengeId,
            @RequestBody UpdateChallengeRequest request,
            Principal principal) { // Spring ne dă userul logat automat

        // Extragem username-ul sau ID-ul userului curent
        // (Aici depinde cum ai tu Auth-ul, presupunem că luăm userul după username)
        ChallengeUserDTO newChallengeUser = service.acceptChallenge(challengeId, principal.getName(), request);

        return ResponseEntity.ok(newChallengeUser);
    }
}
