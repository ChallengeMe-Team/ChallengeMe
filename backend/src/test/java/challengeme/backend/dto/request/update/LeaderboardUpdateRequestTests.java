package challengeme.backend.dto.request.update;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LeaderboardUpdateRequestTests {

    @Test
    void testUpdateRequestOptional() {
        LeaderboardUpdateRequest dto = new LeaderboardUpdateRequest();
        dto.setTotalPoints(200);
        assertEquals(200, dto.getTotalPoints());
    }
}
