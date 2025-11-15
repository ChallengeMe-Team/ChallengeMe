package challengeme.backend.dto.request.update;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationUpdateRequestTests {

    @Test
    void testOptionalIsRead() {
        NotificationUpdateRequest dto = new NotificationUpdateRequest(true);
        assertTrue(dto.isRead());
    }
}
