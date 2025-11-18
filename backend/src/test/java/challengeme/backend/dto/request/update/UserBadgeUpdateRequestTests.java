package challengeme.backend.dto.request.update;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserBadgeUpdateRequestTests {

    @Test
    void testOptionalDateAwarded() {
        UserBadgeUpdateRequest dto = new UserBadgeUpdateRequest();
        LocalDate date = LocalDate.of(2025, 1, 1);
        dto.setDateAwarded(date);

        assertEquals(date, dto.getDateAwarded());
    }
}
