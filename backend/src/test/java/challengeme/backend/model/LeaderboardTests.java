package challengeme.backend.model;

import challengeme.backend.repository.LeaderboardRepository;
import challengeme.backend.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LeaderboardTests {

    private static Validator validator;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private User createUser(String name) {
        User u = new User();
        u.setUsername(name);
        u.setEmail(name + "@email.com");
        u.setPassword("password123");
        u.setPoints(0);
        return userRepository.save(u);
    }

    // ==============================
    // VALIDATION TESTS
    // ==============================
    @Test
    void testValidationSuccess() {
        User u = createUser("ana");
        Leaderboard lb = new Leaderboard(u, 100);
        lb.setRank(1);

        Set<ConstraintViolation<Leaderboard>> violations = validator.validate(lb);
        assertEquals(0, violations.size());
    }

}
