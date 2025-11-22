package challengeme.backend.dto.request;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.model.ChallengeUserStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ChallengeUserDTOTests {

    @Test
    void testDTO() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        ChallengeUserDTO dto = new ChallengeUserDTO(
                userId,
                userId,
                "User",
                challengeId,
                "Challenge",
                ChallengeUserStatus.ACCEPTED,
                LocalDate.now(),
                null
        );

        assertEquals(ChallengeUserStatus.ACCEPTED, dto.getStatus());
        assertEquals("User", dto.getUsername());
        assertEquals(challengeId, dto.getChallengeId());
        assertEquals("Challenge", dto.getChallengeTitle());
    }
}
