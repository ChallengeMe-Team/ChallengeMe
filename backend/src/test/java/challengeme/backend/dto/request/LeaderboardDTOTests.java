package challengeme.backend.dto.request;

import challengeme.backend.dto.LeaderboardDTO;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class LeaderboardDTOTests {

    @Test
    void testDTO() {
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        LeaderboardDTO dto = new LeaderboardDTO(id, userId, "User", 100, 1);

        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals("User", dto.getUsername());
        assertEquals(100, dto.getTotalPoints());
        assertEquals(1, dto.getRank());
    }
}
