package challengeme.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * General purpose User DTO for account management.
 * SECURITY NOTE: Sensitive information such as passwords are never exposed through this object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private UUID id;

    private String username;
    private String email;
    private Integer points;
    private String avatar;
    private String role;

    private Integer totalCompletedChallenges;
}
