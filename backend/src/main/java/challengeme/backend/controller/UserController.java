package challengeme.backend.controller;

import challengeme.backend.dto.FriendDTO;
import challengeme.backend.dto.UserDTO;
import challengeme.backend.dto.request.create.UserCreateRequest;
import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.mapper.UserMapper;
import challengeme.backend.model.User;
import challengeme.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;
import challengeme.backend.dto.request.update.ChangePasswordRequest;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

    // -----------------------------------------------------------
    // GET user friends
    // -----------------------------------------------------------
    @GetMapping("/{id}/friends")
    public ResponseEntity<List<FriendDTO>> getFriends(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserFriends(id));
    }

    // -----------------------------------------------------------
    // SEARCH user by username (used for "Add Friend" feature)
    // -----------------------------------------------------------
    @GetMapping("/search")
    public ResponseEntity<UserDTO> searchByUsername(@RequestParam String username) {
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(mapper.toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // -----------------------------------------------------------
    // ADD friend to list
    // -----------------------------------------------------------
    @PostMapping("/{id}/friends")
    public ResponseEntity<Map<String, String>> addFriend(
            @PathVariable UUID id,
            @RequestParam String username
    ) {
        try {
            userService.addFriend(id, username);
            return ResponseEntity.ok(Map.of("message", "Friend added successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        }
    }

    // -----------------------------------------------------------
    // DEFAULT CRUD
    // -----------------------------------------------------------
    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAllUsers()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(userService.getUserById(id)));
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateRequest request) {
        User created = userService.createUser(mapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable UUID id,
                                          @Valid @RequestBody UserUpdateRequest request) {
        User updated = userService.updateUser(id, request);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Change Password
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable UUID id,
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        try {
            userService.changePassword(id, request);
            // Returnez JSON simplu de succes
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (RuntimeException e) {
            // Returnez eroare (Bad Request sau Forbidden)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
