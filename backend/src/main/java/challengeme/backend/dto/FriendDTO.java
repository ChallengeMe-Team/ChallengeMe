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
    public FriendDTO(UUID id, String username, Integer points) {
        this.id = id;
        this.username = username;
        this.points = points;
    }
}