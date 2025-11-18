package challengeme.backend.dto.request.update;

import challengeme.backend.dto.request.update.ChallengeUpdateRequest;
import challengeme.backend.model.Challenge.Difficulty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeUpdateRequestTests {

    @Test
    void testUpdateRequest() {
        ChallengeUpdateRequest dto = new ChallengeUpdateRequest("New title", null, "Category", Difficulty.HARD, 50, "creator");
        assertEquals("New title", dto.title());
        assertNull(dto.description());
        assertEquals(Difficulty.HARD, dto.difficulty());
        assertEquals(50, dto.points());
    }
}
