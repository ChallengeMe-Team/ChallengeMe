package challengeme.backend.dto.request.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for the user registration (Signup) process.
 * Contains strict security patterns for password complexity and email validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    /** Desired display name for the new account. */
    @NotBlank private String username;

    /** Unique email address for account verification and identification. */
    @NotBlank @Email private String email;

    /** * Account password.
     * Must contain at least one digit, one lowercase letter, one uppercase letter,
     * one special character, and be at least 6 characters long.
     */
    @NotBlank(message = "Password is required.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()\\-_+=<>?/{}\\[\\]|:;\"',~`]).{6,}$",
            message = "Password does not meet security requirements."
    )
    @NotBlank private String password;
}
