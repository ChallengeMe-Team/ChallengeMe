package challengeme.backend.dto.request.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    @NotBlank private String username;
    @NotBlank @Email private String email;
    @NotBlank(message = "Parola este obligatorie.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()\\-_+=<>?/{}\\[\\]|:;\"',~`]).{6,}$",
            message = "Parola nu respectă cerințele de securitate."
    )
    @NotBlank private String password;
}
