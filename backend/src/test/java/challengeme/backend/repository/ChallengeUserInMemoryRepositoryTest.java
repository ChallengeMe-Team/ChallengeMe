package challengeme.backend.repository;

import challengeme.backend.model.ChallengeUser;
import challengeme.backend.model.ChallengeUserStatus;
import challengeme.backend.repository.inMemory.InMemoryChallengeUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeUserInMemoryRepositoryTest {

    private ChallengeUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryChallengeUserRepository();
    }

    @Test
    void testSave_CreateNew() {
        ChallengeUser cu = new ChallengeUser();
        cu.setUserId(UUID.randomUUID());
        cu.setChallengeId(UUID.randomUUID());
        assertNull(cu.getId());

        ChallengeUser savedCu = repository.save(cu);

        assertNotNull(savedCu.getId());
        assertEquals(cu.getUserId(), savedCu.getUserId());

        Optional<ChallengeUser> found = repository.findById(savedCu.getId());
        assertTrue(found.isPresent());
        assertEquals(savedCu.getId(), found.get().getId());
    }

    @Test
    void testSave_UpdateExisting() {
        ChallengeUser cu = new ChallengeUser();
        cu.setUserId(UUID.randomUUID());
        cu.setStatus(ChallengeUserStatus.PENDING);
        ChallengeUser createdCu = repository.save(cu);
        UUID createdId = createdCu.getId();

        createdCu.setStatus(ChallengeUserStatus.COMPLETED);

        ChallengeUser updatedCu = repository.save(createdCu);

        assertEquals(createdId, updatedCu.getId()); // ID-ul trebuie să rămână același
        assertEquals(ChallengeUserStatus.COMPLETED, updatedCu.getStatus());

        Optional<ChallengeUser> found = repository.findById(createdId);
        assertTrue(found.isPresent());
        assertEquals(ChallengeUserStatus.COMPLETED, found.get().getStatus());
        assertEquals(1, repository.findAll().size()); // Verificăm că nu a adăugat o intrare nouă
    }

    @Test
    void testFindById_Found() {
        ChallengeUser savedCu = repository.save(new ChallengeUser());
        UUID id = savedCu.getId();

        Optional<ChallengeUser> found = repository.findById(id);

        assertTrue(found.isPresent());
        assertEquals(id, found.get().getId());
    }

    @Test
    void testFindById_NotFound() {
        UUID randomId = UUID.randomUUID();

        Optional<ChallengeUser> found = repository.findById(randomId);

        assertTrue(found.isEmpty());
    }

    @Test
    void testFindAll() {
        repository.save(new ChallengeUser());
        repository.save(new ChallengeUser());

        List<ChallengeUser> results = repository.findAll();

        assertEquals(2, results.size());
    }

    @Test
    void testFindByUserId() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        ChallengeUser cu1 = new ChallengeUser();
        cu1.setUserId(userId1);
        repository.save(cu1);

        ChallengeUser cu2 = new ChallengeUser();
        cu2.setUserId(userId2);
        repository.save(cu2);

        ChallengeUser cu3 = new ChallengeUser();
        cu3.setUserId(userId1);
        repository.save(cu3);

        List<ChallengeUser> user1Links = repository.findByUserId(userId1);
        List<ChallengeUser> user2Links = repository.findByUserId(userId2);

        assertEquals(2, user1Links.size());
        assertEquals(1, user2Links.size());
        assertEquals(userId1, user1Links.get(0).getUserId());
    }

    @Test
    void testDeleteById() {
        ChallengeUser cu1 = repository.save(new ChallengeUser());
        ChallengeUser cu2 = repository.save(new ChallengeUser());
        UUID idToKeep = cu1.getId();
        UUID idToDelete = cu2.getId();

        assertEquals(2, repository.findAll().size());

        repository.deleteById(idToDelete);

        List<ChallengeUser> results = repository.findAll();
        assertEquals(1, results.size());
        assertEquals(idToKeep, results.get(0).getId());
        assertTrue(repository.findById(idToDelete).isEmpty()); // Verificăm că a fost șters
    }
}