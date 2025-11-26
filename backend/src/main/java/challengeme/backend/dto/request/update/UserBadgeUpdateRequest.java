package challengeme.backend.dto.request.update;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserBadgeUpdateRequest {
    private LocalDate dateAwarded;
}
