package challengeme.backend.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing a badge earned by a specific user.
 * Merges User identity with Badge metadata and the achievement date.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBadgeDTO {
    private UUID id;
    private UUID userId;
    private String username;

    /** Merged badge details for the Trophy Case UI. */
    private UUID badgeId;
    private String badgeName;
    private String description;
    private String iconUrl;
    private Integer pointsReward;

    private LocalDateTime dateAwarded;
}