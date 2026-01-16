package challengeme.backend.dto.request.create;

import lombok.Data;
import java.util.UUID;

/**
 * Request DTO for rewarding a specific badge to a user.
 * Acts as the payload for the achievement unlocking logic.
 */
@Data
public class UserBadgeCreateRequest {
    /** The ID of the user who earned the achievement. */
    private UUID userId;
    /** The ID of the badge being awarded. */
    private UUID badgeId;
}
