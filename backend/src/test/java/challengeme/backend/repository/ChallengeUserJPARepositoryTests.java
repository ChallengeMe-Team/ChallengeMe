package challengeme.backend.repository;

import challengeme.backend.model.Challenge;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.ChallengeUserStatus;
import challengeme.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ChallengeUserJPARepositoryTests {

    @Autowired
    private ChallengeUserRepository challengeUserRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChallengeRepository challengeRepository;

    private User user1;
    private User user2;
    private Challenge challenge1;
    private Challenge challenge2;

    @BeforeEach
    void setup() {
        User u1 = new User(null, "user1", "user1@email.com", "pass123", 10);
        User u2 = new User(null, "user2", "user2@email.com", "pass456", 5);

        Challenge c1 = new Challenge(null, "Challenge1", "Desc1", "Cat1", Challenge.Difficulty.EASY, 50, "creator1");
        Challenge c2 = new Challenge(null, "Challenge2", "Desc2", "Cat2", Challenge.Difficulty.MEDIUM, 100, "creator2");

        user1 = userRepository.save(u1);
        user2 = userRepository.save(u2);
        challenge1 = challengeRepository.save(c1);
        challenge2 = challengeRepository.save(c2);
    }

    @Test
    void testSaveAndFindById() {
        ChallengeUser cu = new ChallengeUser();
        cu.setUser(user1);
        cu.setChallenge(challenge1);
        cu.setStatus(ChallengeUserStatus.PENDING);

        ChallengeUser saved = challengeUserRepository.save(cu);
        assertNotNull(saved.getId());

        UUID id = saved.getId();

        ChallengeUser found = challengeUserRepository.findById(id).orElseThrow();
        assertEquals(ChallengeUserStatus.PENDING, found.getStatus());
        assertEquals(user1.getUsername(), found.getUser().getUsername());
        assertEquals(challenge1.getTitle(), found.getChallenge().getTitle());
    }

    @Test
    void testUpdateStatus() {
        ChallengeUser cu = new ChallengeUser();
        cu.setUser(user1);
        cu.setChallenge(challenge1);
        cu.setStatus(ChallengeUserStatus.PENDING);

        ChallengeUser saved = challengeUserRepository.save(cu);

        saved.setStatus(ChallengeUserStatus.COMPLETED);
        ChallengeUser updated = challengeUserRepository.save(saved);

        ChallengeUser found = challengeUserRepository.findById(updated.getId()).orElseThrow();
        assertEquals(ChallengeUserStatus.COMPLETED, found.getStatus());
    }

    @Test
    void testFindByUserId() {
        ChallengeUser cu1 = new ChallengeUser();
        cu1.setUser(user1);
        cu1.setChallenge(challenge1);
        cu1.setStatus(ChallengeUserStatus.PENDING);

        ChallengeUser cu2 = new ChallengeUser();
        cu2.setUser(user1);
        cu2.setChallenge(challenge2);
        cu2.setStatus(ChallengeUserStatus.COMPLETED);

        ChallengeUser cu3 = new ChallengeUser();
        cu3.setUser(user2);
        cu3.setChallenge(challenge1);
        cu3.setStatus(ChallengeUserStatus.PENDING);

        challengeUserRepository.save(cu1);
        challengeUserRepository.save(cu2);
        challengeUserRepository.save(cu3);

        List<ChallengeUser> user1Links = challengeUserRepository.findByUserId(user1.getId());
        List<ChallengeUser> user2Links = challengeUserRepository.findByUserId(user2.getId());

        assertEquals(2, user1Links.size());
        assertEquals(1, user2Links.size());
        assertTrue(user1Links.stream().allMatch(cu -> cu.getUser().getUsername().equals("user1")));
        assertTrue(user2Links.stream().allMatch(cu -> cu.getUser().getUsername().equals("user2")));
    }

    @Test
    void testFindByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        assertTrue(challengeUserRepository.findById(nonExistentId).isEmpty());
    }

    @Test
    void testDeleteById() {
        ChallengeUser cu = new ChallengeUser();
        cu.setUser(user1);
        cu.setChallenge(challenge1);
        cu.setStatus(ChallengeUserStatus.PENDING);

        ChallengeUser saved = challengeUserRepository.save(cu);

        UUID id = saved.getId();

        challengeUserRepository.deleteById(id);
        assertTrue(challengeUserRepository.findById(id).isEmpty());
    }

    @Test
    void testExistsById() {
        ChallengeUser cu = new ChallengeUser();
        cu.setUser(user1);
        cu.setChallenge(challenge1);
        cu.setStatus(ChallengeUserStatus.PENDING);

        ChallengeUser saved = challengeUserRepository.save(cu);
        assertTrue(challengeUserRepository.existsById(saved.getId()));

        assertFalse(challengeUserRepository.existsById(UUID.randomUUID()));
    }

    @Test
    void testFindAll() {
        ChallengeUser cu1 = new ChallengeUser();
        cu1.setUser(user1);
        cu1.setChallenge(challenge1);
        cu1.setStatus(ChallengeUserStatus.PENDING);

        ChallengeUser cu2 = new ChallengeUser();
        cu2.setUser(user2);
        cu2.setChallenge(challenge2);
        cu2.setStatus(ChallengeUserStatus.COMPLETED);

        challengeUserRepository.save(cu1);
        challengeUserRepository.save(cu2);

        List<ChallengeUser> all = challengeUserRepository.findAll();
        assertEquals(2, all.size());
    }
}