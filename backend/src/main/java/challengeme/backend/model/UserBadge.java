package challengeme.backend.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import challengeme.backend.model.User;
import challengeme.backend.model.Badge;

import java.time.LocalDate;
import java.util.UUID;

/*
 * Represents an association between a User and a Badge.
 * Allows tracking which badges each user has earned and when.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserBadge {

    /** Unique identifier for this user–badge relation. */
    private UUID id;

    /** The user who has earned the badge. Must be provided. */
    @NotNull(message = "User must be provided")
    private User user;

    /** The badge awarded to the user. Must be provided. */
    @NotNull(message = "Badge must be provided")
    private Badge badge;

    /** The date when the badge was awarded to the user. */
    private LocalDate dateAwarded = LocalDate.now();
}
