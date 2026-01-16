package challengeme.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object for social interaction.
 * Provides minimal user data necessary for the "Friends List" UI.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDTO {
    private UUID id;
    private String username;
    private int points;
    private String avatar;
}