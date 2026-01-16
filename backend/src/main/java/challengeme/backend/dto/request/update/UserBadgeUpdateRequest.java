package challengeme.backend.dto.request.update;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request DTO for modifying an achievement record.
 * Allows administrative adjustment of the exact time an achievement was granted.
 */
@Data
public class UserBadgeUpdateRequest {
    private LocalDateTime dateAwarded;
}
