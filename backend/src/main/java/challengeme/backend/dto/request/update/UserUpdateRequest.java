package challengeme.backend.dto.request.update;

/**
 * Comprehensive DTO for user profile management.
 * Designed to handle partial updates; the backend ignores null fields,
 * allowing the user to change only specific attributes like their avatar or email.
 * @param username The updated unique username.
 * @param email The updated contact email.
 * @param password The updated password (if changed via profile settings).
 * @param points Points adjustment (typically for administrative use).
 * @param avatar Path or identifier for the new profile picture.
 */
public record UserUpdateRequest(
        String username,
        String email,
        String password,
        Integer points,
        String avatar
) {}