package challengeme.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

/**
 * Core entity representing a User in the system.
 * Handles authentication credentials, profile metadata, and social connectivity.
 * Includes unique constraints on username and email to ensure identity integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {

    /** Unique identifier generated automatically. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Unique display name for the user, required for identification and social features. */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Column(unique = true)
    private String username;

    /** * List of UUIDs representing the user's friends.
     * Stored as a native PostgreSQL UUID array for optimized social graph queries.
     */
    @Column(name = "friend_ids", columnDefinition = "uuid[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<UUID> friendIds = new ArrayList<>();

    /** Unique email address used for account management and security. */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;

    /** Hashed password for secure authentication. */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
    private String password;

    /** Cumulative experience points earned by completing challenges. */
    private Integer points;

    /** Identifier or path for the user's profile picture. */
    private String avatar;

    /** Security role (e.g., "user", "admin") used for access control. */
    private String role = "user";

    public List<UUID> getFriendIds() {
        if (friendIds == null) {
            friendIds = new ArrayList<>();
        }
        return friendIds;
    }

    /** Global counter for all quests successfully finished by the user. */
    private Integer totalCompletedChallenges = 0; // Inițializează cu 0

    public void setFriendIds(List<UUID> friendIds) {
        this.friendIds = friendIds;
    }
}