package challengeme.backend.repository;

import challengeme.backend.model.Badge;
import challengeme.backend.model.User;
import challengeme.backend.model.UserBadge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserBadgeJPARepositoryTests {

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BadgeRepository badgeRepository;

    private User userA;
    private Badge badgeA;

    @BeforeEach
    void setup() {

        User uA = new User(null, "Ana", "ana@email.com", "secure_pass_A", 0, "user");
        Badge bA = new Badge(null, "Gold", "Top performer badge", "Complete 10 challenges");

        userA = userRepository.save(uA);
        badgeA = badgeRepository.save(bA);
    }

    private UserBadge createUserBadge(User user, Badge badge, LocalDate date) {
        return new UserBadge(null, user, badge, date);
    }

    @Test
    void testSaveCreateAndFindById() {
        UserBadge ub = userBadgeRepository.save(createUserBadge(userA, badgeA, LocalDate.now()));
        UserBadge found = userBadgeRepository.findById(ub.getId()).orElseThrow();

        assertEquals("Gold", found.getBadge().getName());
        assertEquals("Ana", found.getUser().getUsername());

        assertTrue(userBadgeRepository.findById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void testSaveUpdate() {
        UserBadge ub = userBadgeRepository.save(createUserBadge(userA, badgeA, LocalDate.now()));
        ub.setDateAwarded(LocalDate.of(2025, 1, 1));
        userBadgeRepository.save(ub);

        UserBadge updated = userBadgeRepository.findById(ub.getId()).orElseThrow();
        assertEquals(2025, updated.getDateAwarded().getYear());

        assertEquals("Ana", updated.getUser().getUsername());
        assertEquals("Gold", updated.getBadge().getName());
    }

    @Test
    void testFindAllEmptyAndNonEmpty() {
        List<UserBadge> empty = userBadgeRepository.findAll();
        assertTrue(empty.isEmpty());

        userBadgeRepository.save(createUserBadge(userA, badgeA, LocalDate.now()));
        List<UserBadge> all = userBadgeRepository.findAll();
        assertEquals(1, all.size());
    }

    @Test
    void testDeleteById() {
        UserBadge ub = userBadgeRepository.save(createUserBadge(userA, badgeA, LocalDate.now()));

        assertTrue(userBadgeRepository.findById(ub.getId()).isPresent());

        userBadgeRepository.deleteById(ub.getId());
        assertTrue(userBadgeRepository.findById(ub.getId()).isEmpty());
    }

    @Test
    void testMultipleUserBadges() {

        UserBadge ub1 = userBadgeRepository.save(createUserBadge(userA, badgeA, LocalDate.now()));

        User uB = new User(null, "Ion", "ion@email.com", "secure_pass_B", 0, "user");
        User userB = userRepository.save(uB);

        Badge bB = new Badge(null, "Silver", "Runner-up badge", "Complete 5 challenges");
        Badge badgeB = badgeRepository.save(bB);

        UserBadge ub2 = userBadgeRepository.save(createUserBadge(userB, badgeB, LocalDate.now()));

        List<UserBadge> all = userBadgeRepository.findAll();
        assertEquals(2, all.size());

        assertTrue(all.stream().anyMatch(ub -> ub.getUser().getUsername().equals("Ana")));
        assertTrue(all.stream().anyMatch(ub -> ub.getUser().getUsername().equals("Ion")));
    }
}