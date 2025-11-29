package challengeme.backend.dto.request.create;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserBadgeCreateRequestTests {

    @Test
    void testCreateRequest() {
        UUID userId = UUID.randomUUID();
        UUID badgeId = UUID.randomUUID();
        UserBadgeCreateRequest dto = new UserBadgeCreateRequest();
        dto.setUserId(userId);
        dto.setBadgeId(badgeId);

        assertEquals(userId, dto.getUserId());
        assertEquals(badgeId, dto.getBadgeId());
    }
}
