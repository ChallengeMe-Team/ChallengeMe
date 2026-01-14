package challengeme.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

//!!!! Observatie: NU expunem parola în DTO, doar în request.

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
