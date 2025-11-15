package challengeme.backend.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBadgeDTO {
    private UUID id;
    private UUID userId;
    private String username;
    private UUID badgeId;
    private String badgeName;
    private LocalDate dateAwarded;
}
