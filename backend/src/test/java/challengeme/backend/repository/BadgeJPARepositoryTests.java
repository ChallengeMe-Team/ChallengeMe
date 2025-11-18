package challengeme.backend.repository;

import challengeme.backend.model.Badge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BadgeJPARepositoryTests {

    @Autowired
    private BadgeRepository badgeRepository;

    @Test
    void testSaveAndFindAll() {
        Badge badge1 = new Badge(null, "Explorer", "Visited 5 locations", "Visit 5 locations");
        Badge badge2 = new Badge(null, "Achiever", "Completed all tasks", "Complete all challenges");

        badgeRepository.save(badge1);
        badgeRepository.save(badge2);

        List<Badge> badges = badgeRepository.findAll();
        assertEquals(2, badges.size());
        assertTrue(badges.stream().anyMatch(b -> b.getName().equals("Explorer")));
        assertTrue(badges.stream().anyMatch(b -> b.getName().equals("Achiever")));
    }

    @Test
    void testFindById() {
        Badge badge = badgeRepository.save(new Badge(null, "Explorer", "Desc", "Criteria"));
        Badge found = badgeRepository.findById(badge.getId()).orElseThrow();
        assertEquals("Explorer", found.getName());
    }

    @Test
    void testDelete() {
        Badge badge = badgeRepository.save(new Badge(null, "Explorer", "Desc", "Criteria"));
        UUID id = badge.getId();
        badgeRepository.deleteById(id);
        assertTrue(badgeRepository.findById(id).isEmpty());
    }

    @Test
    void testUpdate() {
        Badge badge = badgeRepository.save(new Badge(null, "Explorer", "Desc", "Criteria"));
        badge.setName("Explorer Updated");
        badge.setDescription("Updated description");
        badgeRepository.save(badge);

        Badge updated = badgeRepository.findById(badge.getId()).orElseThrow();
        assertEquals("Explorer Updated", updated.getName());
        assertEquals("Updated description", updated.getDescription());
    }

    @Test
    void testDeleteNonExisting() {
        UUID randomId = UUID.randomUUID();
        assertDoesNotThrow(() -> badgeRepository.deleteById(randomId));
        assertTrue(badgeRepository.findAll().isEmpty());
    }
}
