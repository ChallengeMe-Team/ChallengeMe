package challengeme.backend.dto.request.update;

import challengeme.backend.dto.request.update.BadgeUpdateRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadgeUpdateRequestTests {

    @Test
    void testSettersAndGetters() {
        BadgeUpdateRequest dto = new BadgeUpdateRequest("Silver", "Runner-up", "Complete 5 challenges");
        assertEquals("Silver", dto.name());
        assertEquals("Runner-up", dto.description());
        assertEquals("Complete 5 challenges", dto.criteria());
    }
}
