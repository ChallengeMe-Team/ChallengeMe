package challengeme.backend.dto.request;

import challengeme.backend.dto.ChallengeDTO;
import challengeme.backend.model.Challenge.Difficulty;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeDTOTests {

    @Test
    void testChallengeDTO() {
        UUID id = UUID.randomUUID();
        ChallengeDTO dto = new ChallengeDTO(id, "Title", "Desc", "Cat", Difficulty.EASY, 10, "creator");
        assertEquals(id, dto.id());
        assertEquals("Title", dto.title());
        assertEquals(Difficulty.EASY, dto.difficulty());
        assertEquals(10, dto.points());
    }
}
