package challengeme.backend.dto.request.create;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class LeaderboardCreateRequestTests {

    @Test
    void testCreateRequest() {
        UUID userId = UUID.randomUUID();
        LeaderboardCreateRequest dto = new LeaderboardCreateRequest();
        dto.setUserId(userId);
        dto.setTotalPoints(50);

        assertEquals(userId, dto.getUserId());
        assertEquals(50, dto.getTotalPoints());
    }
}
