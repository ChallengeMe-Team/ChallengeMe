package challengeme.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entity representing the 'badges' table in the database.
 * Defines the static attributes of an achievement that can be earned by users.
 */
@Entity
@Table(name = "badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Badge {

    /** Unique identifier for the badge, generated automatically as a UUID. */
    @Id
    @GeneratedValue
    private UUID id;

    /** The display name of the badge (e.g., 'Early Bird', 'Consistency King'). */
    @NotBlank(message = "Name is required")
    private String name;

    /** A text describing what the badge represents or the achievement it marks. */
    @NotBlank(message = "Description is required")
    private String description;

    /** Optional text defining the specific rules or conditions to unlock this badge. */
    private String criteria;

    /** Path or URL to the visual icon of the badge. */
    private String iconUrl;

    /** Points rewarded to the user's total score upon unlocking this badge. */
    private int pointsReward;
}
