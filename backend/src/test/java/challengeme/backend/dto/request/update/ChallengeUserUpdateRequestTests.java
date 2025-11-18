package challengeme.backend.dto.request.update;

import challengeme.backend.dto.request.update.ChallengeUserUpdateRequest;
import challengeme.backend.model.ChallengeUserStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChallengeUserUpdateRequestTests {

    @Test
    void testOptionalStatus() {
        ChallengeUserUpdateRequest dto = new ChallengeUserUpdateRequest();
        dto.setStatus(ChallengeUserStatus.COMPLETED);
        assertEquals(ChallengeUserStatus.COMPLETED, dto.getStatus());
    }
}
