package challengeme.backend.dto.request.update;

import jakarta.validation.constraints.NotBlank;

// DTO pentru schimbarea parolei
public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {}