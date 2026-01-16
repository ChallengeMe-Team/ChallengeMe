package challengeme.backend.controller;

import challengeme.backend.dto.FriendDTO;
import challengeme.backend.dto.UserDTO;
import challengeme.backend.dto.UserProfileDTO;
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
import challengeme.backend.security.JwtUtils;

/**
 * Controller responsible for managing user-related operations.
 * It handles profile management, social features (friendships), account settings,
 * and availability checks for credentials.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;
    private final JwtUtils jwtUtils;

    /**
     * Retrieves the list of friends for a specific user.
     * @param id The UUID of the user.
     * @return a ResponseEntity containing a list of FriendDTO objects.
     */
    @GetMapping("/{id}/friends")
    public ResponseEntity<List<FriendDTO>> getFriends(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserFriends(id));
    }

    /**
     * Searches for a user by their exact username.
     * Primarily used in the "Add Friend" search interface.
     * @param username The username to search for.
     * @return a ResponseEntity with the UserDTO or 404 Not Found.
     */
    @GetMapping("/search")
    public ResponseEntity<UserDTO> searchByUsername(@RequestParam String username) {
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(mapper.toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Establishes a friendship link between a user and another user (by username).
     * @param id The UUID of the user initiating the request.
     * @param username The username of the friend to be added.
     * @return 200 OK with success message or 400 Bad Request on error.
     */
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

    /**
     * Removes a friendship link between two users.
     * @param id The UUID of the user initiating the removal.
     * @param friendId The UUID of the friend to be removed.
     * @return 200 OK with success message or 400 Bad Request on error.
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable UUID id, @PathVariable UUID friendId) {
        try {
            userService.removeFriend(id, friendId);
            return ResponseEntity.ok(Map.of("message", "Friend removed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Triggers a global synchronization of friendship links to ensure data consistency.
     * @return a map containing the number of synchronized relationships.
     */
    @PostMapping("/sync-friends")
    public ResponseEntity<Map<String, Integer>> syncFriends() {
        return ResponseEntity.ok(userService.syncAllFriendships());
    }

    /**
     * Retrieves all users registered in the system.
     * @return a list of UserDTO objects.
     */
    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAllUsers()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * Retrieves specific user data by their ID.
     * @param id The unique UUID of the user.
     * @return the found UserDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(userService.getUserById(id)));
    }

    /**
     * Creates a new user entry. Used for administrative purposes.
     * @param request The DTO containing the data for the new user.
     * @return the created UserDTO with 201 Created status.
     */
    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateRequest request) {
        User created = userService.createUser(mapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(created));
    }

    /**
     * Updates user profile details. If the username is changed, a new JWT token
     * is generated to ensure the session remains valid.
     * @param id The UUID of the user to update.
     * @param request The DTO containing updated profile data.
     * @return a map containing the updated user DTO and a new authentication token.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @Valid @RequestBody UserUpdateRequest request) {
        try {
            User updatedUser = userService.updateUser(id, request);

            String newToken = jwtUtils.generateTokenFromUsername(updatedUser.getUsername());

            UserDTO userDTO = mapper.toDTO(updatedUser);

            Map<String, Object> response = new HashMap<>();
            response.put("user", userDTO);
            response.put("token", newToken);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Checks if a username is already taken.
     * Used for real-time validation in the frontend registration/profile forms.
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     */
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsernameAvailability(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    /**
     * Checks if an email is already associated with an account.
     * Used for real-time validation in the frontend.
     * @param email The email address to check.
     * @return true if the email exists, false otherwise.
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailAvailability(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    /**
     * Removes a user from the system.
     * @param id The UUID of the user to delete.
     * @return 204 No Content status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Securely updates a user's password.
     * @param id The UUID of the user.
     * @param request DTO containing the old and new passwords.
     * @return 200 OK on success or 400 Bad Request on failure.
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable UUID id,
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        try {
            userService.changePassword(id, request);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Retrieves the profile data of the currently authenticated user.
     * @return the UserProfileDTO containing aggregated stats and activity history.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    /**
     * Retrieves the public profile data of a specific user by their ID.
     * @param id The UUID of the user.
     * @return the UserProfileDTO of the target user.
     */
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserProfileById(id));
    }
}
