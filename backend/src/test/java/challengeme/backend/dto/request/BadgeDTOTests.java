package challengeme.backend.dto.request;

import challengeme.backend.dto.BadgeDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BadgeDTOTests {

    @Test
    void testBadgeDTO() {
        UUID id = UUID.randomUUID();
        BadgeDTO dto = new BadgeDTO(id, "Gold", "Top performer", "Complete 10 challenges");

        assertEquals(id, dto.id());
        assertEquals("Gold", dto.name());
        assertEquals("Top performer", dto.description());
        assertEquals("Complete 10 challenges", dto.criteria());
    }
}
