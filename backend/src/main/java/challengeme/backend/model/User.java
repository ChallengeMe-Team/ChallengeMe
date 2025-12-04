package challengeme.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

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

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Column(unique = true)
    private String username;

    @Column(name = "friend_ids", columnDefinition = "uuid[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<UUID> friendIds = new ArrayList<>();

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
    private String password;

    private Integer points;

    private String avatar;

    private String role = "user";

    // Getters and Setters explicite pentru siguranță, deși Lombok le generează
    public List<UUID> getFriendIds() {
        if (friendIds == null) {
            friendIds = new ArrayList<>();
        }
        return friendIds;
    }

    public void setFriendIds(List<UUID> friendIds) {
        this.friendIds = friendIds;
    }
}