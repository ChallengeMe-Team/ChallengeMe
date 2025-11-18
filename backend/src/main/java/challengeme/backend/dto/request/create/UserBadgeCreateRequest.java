package challengeme.backend.dto.request.create;

import lombok.Data;
import java.util.UUID;

@Data
public class UserBadgeCreateRequest {
    private UUID userId;
    private UUID badgeId;
}
