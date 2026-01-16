package challengeme.backend.dto.request.update;

import jakarta.validation.constraints.NotBlank;

/**
 * Security-focused DTO for user password rotation.
 * Ensures that the current credentials are confirmed before setting a new password.
 * @param currentPassword The user's existing password for verification.
 * @param newPassword The desired new password (should follow complexity patterns).
 */
public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {}