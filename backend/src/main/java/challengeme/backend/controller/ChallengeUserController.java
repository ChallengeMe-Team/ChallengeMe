package challengeme.backend.controller;

import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.CreateChallengeUserRequest;
import challengeme.backend.model.UpdateChallengeStatusRequest;
import challengeme.backend.service.ChallengeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller-ul REST pentru gestionarea legăturilor User-Challenge.
 */
@RestController
@RequestMapping("/api/v1/user-challenges") // prefix bun pentru API
@RequiredArgsConstructor
public class ChallengeUserController {

    private final ChallengeUserService challengeUserService;

    @PostMapping
    public ResponseEntity<ChallengeUser> createChallengeUserLink(@RequestBody CreateChallengeUserRequest request) {
        // Aici se poate adăuga validare pe request body cu @Valid
        ChallengeUser createdLink = challengeUserService.createChallengeUser(request);
        return new ResponseEntity<>(createdLink, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ChallengeUser>> getAllChallengeUserLinks() {
        List<ChallengeUser> links = challengeUserService.getAllChallengeUsers();
        return ResponseEntity.ok(links);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeUser> getChallengeUserLinkById(@PathVariable UUID id) {
        ChallengeUser link = challengeUserService.getChallengeUserById(id);
        return ResponseEntity.ok(link);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChallengeUser>> getChallengeUserLinksByUserId(@PathVariable UUID userId) {
        List<ChallengeUser> links = challengeUserService.getChallengeUsersByUserId(userId);
        return ResponseEntity.ok(links);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ChallengeUser> updateChallengeUserLinkStatus(@PathVariable UUID id, @RequestBody UpdateChallengeStatusRequest request) {
        if (request.getStatus() == null) {
            return ResponseEntity.badRequest().build();
        }
        ChallengeUser updatedLink = challengeUserService.updateChallengeUserStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedLink);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallengeUserLink(@PathVariable UUID id) {
        challengeUserService.deleteChallengeUser(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }
}
