package challengeme.backend.dto.request.update;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBadgeUpdateRequest {
    private LocalDateTime dateAwarded;
}
