package challengeme.backend.mapper;

import challengeme.backend.dto.FriendDTO;
import challengeme.backend.dto.request.create.UserCreateRequest;
import challengeme.backend.dto.UserDTO;
import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper component responsible for converting User entities to various DTO formats.
 * It handles profile data transformation, friend list simplification,
 * and secure partial updates of user account details.
 */
@Component
public class UserMapper {

    /**
     * Converts a User entity into a comprehensive UserDTO.
     * Includes role and statistical data, but strictly excludes sensitive fields like passwords.
     * @param user The User entity from the database.
     * @return A UserDTO for general account management and display.
     */
    public UserDTO toDTO(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPoints(),
                user.getAvatar(),
                user.getRole(),
                user.getTotalCompletedChallenges() != null ? user.getTotalCompletedChallenges() : 0
        );
    }

    /**
     * Converts a User entity into a simplified FriendDTO.
     * Optimized for social lists where only basic public info (points, avatar) is required.
     * @param user The User entity to be transformed.
     * @return A lightweight DTO for friendship modules.
     */
    public FriendDTO toFriendDTO(User user) {
        if (user == null) return null;
        return new FriendDTO(
                user.getId(),
                user.getUsername(),
                user.getPoints(),
                user.getAvatar()
        );
    }

    /**
     * Maps a UserCreateRequest to a new User entity.
     * Initializes default values such as role and avatar for new registrations.
     * @param request The DTO containing registration details.
     * @return A new User entity ready for persistence.
     */
    public User toEntity(UserCreateRequest request) {
        if (request == null) return null;
        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPoints(0);
        // Sets default visual identity for new users
        user.setAvatar("gamer.png");
        user.setRole("user");
        return user;
    }

    /**
     * Updates an existing User entity with data from a UserUpdateRequest.
     * Supports partial updates by checking for non-null fields.
     * Security: The user's role cannot be modified through this method.
     * @param request The DTO containing the fields to be updated.
     * @param user The existing User entity to be modified.
     */
    public void updateEntity(UserUpdateRequest request, User user) {
        if (request.username() != null) user.setUsername(request.username());
        if (request.email() != null) user.setEmail(request.email());
        if (request.password() != null) user.setPassword(request.password());
        if (request.points() != null) user.setPoints(request.points());
        if (request.avatar() != null) user.setAvatar(request.avatar());
    }

}
