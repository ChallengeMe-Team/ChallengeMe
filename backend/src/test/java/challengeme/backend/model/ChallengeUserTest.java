package challengeme.backend.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ChallengeUserTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        ChallengeUser challengeUser = new ChallengeUser();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        challengeUser.setId(id);
        challengeUser.setUserId(userId);
        challengeUser.setChallengeId(challengeId);
        challengeUser.setStatus(ChallengeUserStatus.COMPLETED);
        challengeUser.setDateAccepted(date);
        challengeUser.setDateCompleted(date);

        assertEquals(id, challengeUser.getId());
        assertEquals(userId, challengeUser.getUserId());
        assertEquals(challengeId, challengeUser.getChallengeId());
        assertEquals(ChallengeUserStatus.COMPLETED, challengeUser.getStatus());
        assertEquals(date, challengeUser.getDateAccepted());
        assertEquals(date, challengeUser.getDateCompleted());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        ChallengeUserStatus status = ChallengeUserStatus.ACCEPTED;
        LocalDate dateAccepted = LocalDate.now().minusDays(1);
        LocalDate dateCompleted = LocalDate.now();

        ChallengeUser challengeUser = new ChallengeUser(
                id,
                userId,
                challengeId,
                status,
                dateAccepted,
                dateCompleted
        );

        assertEquals(id, challengeUser.getId());
        assertEquals(userId, challengeUser.getUserId());
        assertEquals(challengeId, challengeUser.getChallengeId());
        assertEquals(status, challengeUser.getStatus());
        assertEquals(dateAccepted, challengeUser.getDateAccepted());
        assertEquals(dateCompleted, challengeUser.getDateCompleted());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        ChallengeUser cu1 = new ChallengeUser(id, userId, challengeId, ChallengeUserStatus.PENDING, date, null);
        ChallengeUser cu2 = new ChallengeUser(id, userId, challengeId, ChallengeUserStatus.PENDING, date, null);
        ChallengeUser cu3 = new ChallengeUser(UUID.randomUUID(), userId, challengeId, ChallengeUserStatus.PENDING, date, null); // ID diferit

        assertEquals(cu1, cu2);
        assertNotEquals(cu1, cu3);
        assertNotEquals(cu1, null);
        assertNotEquals(cu1, new Object());

        assertEquals(cu1.hashCode(), cu2.hashCode());
        assertNotEquals(cu1.hashCode(), cu3.hashCode());
    }

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        ChallengeUser cu = new ChallengeUser();
        cu.setId(id);

        String s = cu.toString();

        assertTrue(s.contains("id=" + id.toString()));
        assertTrue(s.contains("ChallengeUser("));
    }
}