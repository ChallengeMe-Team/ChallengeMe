package challengeme.backend.model;

import challengeme.backend.repository.BadgeRepository;
import challengeme.backend.repository.UserBadgeRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserBadgeTests {

    private static Validator validator;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==============================
    // VALIDATION TESTS
    // ==============================
    @Test
    void testValidationSuccess() {
        User user = new User();
        user.setUsername("Ana");
        user.setEmail("ana@email.com");
        user.setPassword("secret123");
        user.setPoints(10);
        userRepository.save(user);

        Badge badge = new Badge();
        badge.setName("Gold");
        badge.setDescription("Top performer badge");
        badgeRepository.save(badge);

        UserBadge userBadge = new UserBadge(null, user, badge, LocalDate.now());
        Set<ConstraintViolation<UserBadge>> violations = validator.validate(userBadge);
        assertEquals(0, violations.size());
    }

    @Test
    void testValidationFail_UserNull() {
        Badge badge = new Badge();
        badge.setName("Gold");
        badge.setDescription("Top performer badge");
        badgeRepository.save(badge);

        UserBadge userBadge = new UserBadge(null, null, badge, LocalDate.now());
        Set<ConstraintViolation<UserBadge>> violations = validator.validate(userBadge);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("user")));
    }

    @Test
    void testValidationFail_BadgeNull() {
        User user = new User();
        user.setUsername("Ana");
        user.setEmail("ana@email.com");
        user.setPassword("secret123");
        user.setPoints(10);
        userRepository.save(user);

        UserBadge userBadge = new UserBadge(null, user, null, LocalDate.now());
        Set<ConstraintViolation<UserBadge>> violations = validator.validate(userBadge);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("badge")));
    }

    // ==============================
    // JPA PERSISTENCE TESTS
    // ==============================
    @Test
    void testSaveAndRetrieveUserBadge() {
        User user = new User();
        user.setUsername("Ion");
        user.setEmail("ion@email.com");
        user.setPassword("pass1234");
        user.setPoints(5);
        userRepository.save(user);

        Badge badge = new Badge();
        badge.setName("Silver");
        badge.setDescription("Runner-up badge");
        badgeRepository.save(badge);

        UserBadge userBadge = new UserBadge(null, user, badge, LocalDate.now());
        UserBadge saved = userBadgeRepository.save(userBadge);

        assertNotNull(saved.getId());
        assertEquals(user, saved.getUser());
        assertEquals(badge, saved.getBadge());
        assertNotNull(saved.getDateAwarded());

        UserBadge found = userBadgeRepository.findById(saved.getId()).orElseThrow();
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void testUpdateUserBadgeDate() {
        User user = new User();
        user.setUsername("Maria");
        user.setEmail("maria@email.com");
        user.setPassword("pass123");
        user.setPoints(20);
        userRepository.save(user);

        Badge badge = new Badge();
        badge.setName("Bronze");
        badge.setDescription("Participant badge");
        badgeRepository.save(badge);

        UserBadge userBadge = new UserBadge(null, user, badge, LocalDate.of(2025, 1, 1));
        UserBadge saved = userBadgeRepository.save(userBadge);

        saved.setDateAwarded(LocalDate.of(2025, 2, 1));
        UserBadge updated = userBadgeRepository.save(saved);

        assertEquals(LocalDate.of(2025, 2, 1), updated.getDateAwarded());
    }

    @Test
    void testDeleteUserBadge() {
        User user = new User();
        user.setUsername("Alex");
        user.setEmail("alex@email.com");
        user.setPassword("pass123");
        user.setPoints(15);
        userRepository.save(user);

        Badge badge = new Badge();
        badge.setName("Gold");
        badge.setDescription("Top performer");
        badgeRepository.save(badge);

        UserBadge userBadge = new UserBadge(null, user, badge, LocalDate.now());
        UserBadge saved = userBadgeRepository.save(userBadge);

        long countBefore = userBadgeRepository.count();
        userBadgeRepository.deleteById(saved.getId());
        long countAfter = userBadgeRepository.count();

        assertEquals(countBefore - 1, countAfter);
    }

    @Test
    void testEqualsAndHashCodeBasedOnId() {
        User user = new User();
        user.setUsername("Luca");
        user.setEmail("luca@email.com");
        user.setPassword("pass123");
        user.setPoints(30);
        userRepository.save(user);

        Badge badge = new Badge();
        badge.setName("Platinum");
        badge.setDescription("Elite badge");
        badgeRepository.save(badge);

        UserBadge ub1 = new UserBadge(null, user, badge, LocalDate.now());
        UserBadge ub2 = new UserBadge(null, user, badge, LocalDate.now());

        UserBadge saved1 = userBadgeRepository.save(ub1);
        UserBadge saved2 = userBadgeRepository.save(ub2);

        assertNotEquals(saved1, saved2);
        assertNotEquals(saved1.hashCode(), saved2.hashCode());

        // Set same ID => should be equal
        saved2.setId(saved1.getId());
        assertEquals(saved1, saved2);
        assertEquals(saved1.hashCode(), saved2.hashCode());
    }
}
