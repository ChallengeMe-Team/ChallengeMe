package challengeme.backend.dto.request.update;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserUpdateRequestTests {

    @Test
    void testOptionalFields() {
        UserUpdateRequest dto = new UserUpdateRequest("NewUser", null, null, 50);

        assertEquals("NewUser", dto.username());
        assertNull(dto.email());
        assertNull(dto.password());
        assertEquals(50, dto.points());
    }
}
