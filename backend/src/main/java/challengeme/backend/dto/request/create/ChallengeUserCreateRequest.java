package challengeme.backend.dto.request.create;

import lombok.Data;
import java.util.UUID;

/**
 * Request DTO used to link a User to a specific Challenge.
 * Primarily used for administrative assignments or initial participation tracking.
 */
@Data
public class ChallengeUserCreateRequest {
    /** The unique identifier of the user participating in the challenge. */
    private UUID userId;
    /** The unique identifier of the challenge to be accepted/assigned. */
    private UUID challengeId;
}
