package challengeme.backend.dto.request;

import challengeme.backend.dto.UserDTO;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserDTOTests {

    @Test
    void testDTO() {
        UUID id = UUID.randomUUID();
        UserDTO dto = new UserDTO(id, "User", "user@email.com",  100, "",  "user", 0);

        assertEquals(id, dto.getId());
        assertEquals("User", dto.getUsername());
        assertEquals("user@email.com", dto.getEmail());
        assertEquals(100, dto.getPoints());
    }
}
