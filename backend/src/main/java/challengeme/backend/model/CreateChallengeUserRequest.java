package challengeme.backend.model;

import lombok.Data;

import java.util.UUID;

/**
 * DTO (Data Transfer Object) folosit pentru a crea o nouă legătură
 * între un utilizator și o provocare.
 */
@Data
public class CreateChallengeUserRequest {
    // Validarea se poate adăuga aici cu @NotNull
    private UUID userId;
    private UUID challengeId;
}
