package challengeme.backend.dto.request;

import challengeme.backend.dto.UserBadgeDTO;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserBadgeDTOTests {

    @Test
    void testDTO() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID badgeId = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        UserBadgeDTO dto = new UserBadgeDTO(id, userId, "User", badgeId, "Gold", date);

        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals("User", dto.getUsername());
        assertEquals(badgeId, dto.getBadgeId());
        assertEquals("Gold", dto.getBadgeName());
        assertEquals(date, dto.getDateAwarded());
    }
}
