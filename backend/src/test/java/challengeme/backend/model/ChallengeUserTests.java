package challengeme.backend.model;

import challengeme.backend.repository.ChallengeRepository;
import challengeme.backend.repository.ChallengeUserRepository;
import challengeme.backend.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ChallengeUserTests {

    private static Validator validator;

    @Autowired
    private ChallengeUserRepository challengeUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ================================
    // VALIDATION TESTS
    // ================================
    @Test
    void testValidationSuccess() {
        User user = new User(null, "User1", "user1@email.com", "password123", 0);
        userRepository.save(user);

        Challenge challenge = new Challenge(null, "Title", "Desc", "Cat", Challenge.Difficulty.EASY, 10, "creator");
        challengeRepository.save(challenge);

        ChallengeUser cu = new ChallengeUser(null, user, challenge, ChallengeUserStatus.PENDING, LocalDate.now(), null);

        Set<ConstraintViolation<ChallengeUser>> violations = validator.validate(cu);
        assertEquals(0, violations.size());
    }

    @Test
    void testValidationFail_UserNull() {
        Challenge challenge = new Challenge(null, "Title", "Desc", "Cat", Challenge.Difficulty.EASY, 10, "creator");
        challengeRepository.save(challenge);

        ChallengeUser cu = new ChallengeUser(null, null, challenge, ChallengeUserStatus.PENDING, LocalDate.now(), null);

        Set<ConstraintViolation<ChallengeUser>> violations = validator.validate(cu);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("user")));
    }

    @Test
    void testValidationFail_ChallengeNull() {
        User user = new User(null, "User2", "user2@email.com", "password123", 0);
        userRepository.save(user);

        ChallengeUser cu = new ChallengeUser(null, user, null, ChallengeUserStatus.PENDING, LocalDate.now(), null);

        Set<ConstraintViolation<ChallengeUser>> violations = validator.validate(cu);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("challenge")));
    }

    // ================================
    // JPA PERSISTENCE TESTS
    // ================================
    @Test
    void testSaveAndRetrieveChallengeUser() {
        User user = new User(null, "User3", "user3@email.com", "password123", 0);
        userRepository.save(user);

        Challenge challenge = new Challenge(null, "Title3", "Desc3", "Cat", Challenge.Difficulty.MEDIUM, 20, "creator");
        challengeRepository.save(challenge);

        ChallengeUser cu = new ChallengeUser(null, user, challenge, ChallengeUserStatus.ACCEPTED, LocalDate.now(), null);
        ChallengeUser saved = challengeUserRepository.save(cu);

        assertNotNull(saved.getId());
        assertEquals(ChallengeUserStatus.ACCEPTED, saved.getStatus());

        ChallengeUser found = challengeUserRepository.findById(saved.getId()).orElseThrow();
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void testUpdateChallengeUserStatusAndDates() {
        User user = new User(null, "User4", "user4@email.com", "password123", 0);
        userRepository.save(user);

        Challenge challenge = new Challenge(null, "Title4", "Desc4", "Cat", Challenge.Difficulty.HARD, 30, "creator");
        challengeRepository.save(challenge);

        ChallengeUser cu = new ChallengeUser(null, user, challenge, ChallengeUserStatus.PENDING, null, null);
        ChallengeUser saved = challengeUserRepository.save(cu);

        saved.setStatus(ChallengeUserStatus.COMPLETED);
        saved.setDateAccepted(LocalDate.now().minusDays(1));
        saved.setDateCompleted(LocalDate.now());
        ChallengeUser updated = challengeUserRepository.save(saved);

        assertEquals(ChallengeUserStatus.COMPLETED, updated.getStatus());
        assertNotNull(updated.getDateAccepted());
        assertNotNull(updated.getDateCompleted());
    }

    @Test
    void testDeleteChallengeUser() {
        User user = new User(null, "User5", "user5@email.com", "password123", 0);
        userRepository.save(user);

        Challenge challenge = new Challenge(null, "Title5", "Desc5", "Cat", Challenge.Difficulty.EASY, 10, "creator");
        challengeRepository.save(challenge);

        ChallengeUser cu = new ChallengeUser(null, user, challenge, ChallengeUserStatus.PENDING, LocalDate.now(), null);
        ChallengeUser saved = challengeUserRepository.save(cu);

        long countBefore = challengeUserRepository.count();
        challengeUserRepository.deleteById(saved.getId());
        long countAfter = challengeUserRepository.count();

        assertEquals(countBefore - 1, countAfter);
    }

    @Test
    void testEqualsAndHashCodeBasedOnId() {
        User user = new User(null, "User6", "user6@email.com", "password123", 0);
        userRepository.save(user);

        Challenge challenge = new Challenge(null, "Title6", "Desc6", "Cat", Challenge.Difficulty.MEDIUM, 15, "creator");
        challengeRepository.save(challenge);

        ChallengeUser cu1 = new ChallengeUser(null, user, challenge, ChallengeUserStatus.PENDING, LocalDate.now(), null);
        ChallengeUser cu2 = new ChallengeUser(null, user, challenge, ChallengeUserStatus.PENDING, LocalDate.now(), null);

        ChallengeUser saved1 = challengeUserRepository.save(cu1);
        ChallengeUser saved2 = challengeUserRepository.save(cu2);

        assertNotEquals(saved1, saved2);
        assertNotEquals(saved1.hashCode(), saved2.hashCode());
    }

}
