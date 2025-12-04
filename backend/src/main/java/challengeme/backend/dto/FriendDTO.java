package challengeme.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDTO {
    private UUID id;
    private String username;
    private int points;
    private String avatar;
    public FriendDTO(UUID id, String username, Integer points, String avatar) {
        this.id = id;
        this.username = username;
        this.points = points;
        this.avatar = avatar;
    }
}