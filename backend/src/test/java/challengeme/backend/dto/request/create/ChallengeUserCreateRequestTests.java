package challengeme.backend.dto.request.create;

import challengeme.backend.dto.request.create.ChallengeUserCreateRequest;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ChallengeUserCreateRequestTests {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidCreate() {
        ChallengeUserCreateRequest dto = new ChallengeUserCreateRequest();
        dto.setUserId(UUID.randomUUID());
        dto.setChallengeId(UUID.randomUUID());
        assertTrue(validator.validate(dto).isEmpty());
    }
}
