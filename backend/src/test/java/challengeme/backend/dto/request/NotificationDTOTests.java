package challengeme.backend.dto;

import challengeme.backend.model.NotificationType;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class NotificationDTOTests {

    @Test
    void testDTO() {
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        NotificationDTO dto = new NotificationDTO(id, userId, "Message", NotificationType.SYSTEM, timestamp, false);

        assertEquals(id, dto.id());
        assertEquals(userId, dto.userId());
        assertEquals("Message", dto.message());
        assertEquals(NotificationType.SYSTEM, dto.type());
        assertEquals(timestamp, dto.timestamp());
        assertFalse(dto.isRead());
    }
}
