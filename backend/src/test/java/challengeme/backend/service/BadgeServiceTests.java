package challengeme.backend.service;

import challengeme.backend.exception.BadgeNotFoundException;
import challengeme.backend.model.Badge;
import challengeme.backend.repository.BadgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BadgeServiceTests {

    private BadgeRepository badgeRepository;
    private BadgeService badgeService;

    @BeforeEach
    void setup() {
        badgeRepository = Mockito.mock(BadgeRepository.class);
        badgeService = new BadgeService(badgeRepository);
    }

    @Test
    void testGetAllBadges() {
        List<Badge> badges = Arrays.asList(
                new Badge(UUID.randomUUID(), "Explorer", "Visited 5 locations", "Visit 5 locations"),
                new Badge(UUID.randomUUID(), "Achiever", "Completed all tasks", "Complete all challenges")
        );

        when(badgeRepository.findAll()).thenReturn(badges);

        List<Badge> result = badgeService.getAllBadges();
        assertEquals(badges, result);

        verify(badgeRepository, times(1)).findAll();
    }

    @Test
    void testGetBadgeById() {
        Badge badge = new Badge(UUID.randomUUID(), "Explorer", "Visited 5 locations", "Visit 5 locations");
        when(badgeRepository.findById(badge.getId())).thenReturn(badge);

        Badge result = badgeService.getBadgeById(badge.getId());
        assertEquals(badge, result);

        verify(badgeRepository, times(1)).findById(badge.getId());
    }

    @Test
    void testCreateBadgeWithId() {
        Badge badge = new Badge(UUID.randomUUID(), "Achiever", "Completed all tasks", "Complete all challenges");

        when(badgeRepository.save(badge)).thenReturn(badge);

        Badge created = badgeService.createBadge(badge);
        assertEquals(badge, created);
        verify(badgeRepository, times(1)).save(badge);
    }

    @Test
    void testCreateBadgeWithoutId() {
        Badge badge = new Badge(null, "Explorer", "Visited 5 locations", "Visit 5 locations");

        when(badgeRepository.save(any(Badge.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Badge created = badgeService.createBadge(badge);
        assertNotNull(created.getId());
        verify(badgeRepository, times(1)).save(created);
    }

    @Test
    void testDeleteBadge() {
        UUID id = UUID.randomUUID();
        doNothing().when(badgeRepository).delete(id);

        badgeService.deleteBadge(id);

        verify(badgeRepository, times(1)).delete(id);
    }

    @Test
    void testDeleteBadgeNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new BadgeNotFoundException("Badge with id " + id + " not found"))
                .when(badgeRepository).delete(id);

        assertThrows(BadgeNotFoundException.class, () -> badgeService.deleteBadge(id));
        verify(badgeRepository, times(1)).delete(id);
    }

    @Test
    void testUpdateBadge() {
        UUID id = UUID.randomUUID();
        Badge existingBadge = new Badge(id, "Explorer", "Visited 5 locations", "Visit 5 locations");
        Badge updatedBadge = new Badge(null, "Explorer Updated", "Visited 10 locations", "Visit 10 locations");

        when(badgeRepository.findById(id)).thenReturn(existingBadge);
        doNothing().when(badgeRepository).update(any(Badge.class));

        Badge result = badgeService.updateBadge(id, updatedBadge);

        assertEquals(id, result.getId());
        assertEquals("Explorer Updated", result.getName());
        assertEquals("Visited 10 locations", result.getDescription());
        assertEquals("Visit 10 locations", result.getCriteria());

        verify(badgeRepository, times(1)).findById(id);
        verify(badgeRepository, times(1)).update(updatedBadge);
    }

    @Test
    void testUpdateBadgeNotFound() {
        UUID id = UUID.randomUUID();
        Badge updatedBadge = new Badge(null, "NonExistent", "Does not exist", "No criteria");

        when(badgeRepository.findById(id)).thenThrow(new BadgeNotFoundException("Badge with id " + id + " not found"));

        assertThrows(BadgeNotFoundException.class, () -> badgeService.updateBadge(id, updatedBadge));

        verify(badgeRepository, times(1)).findById(id);
        verify(badgeRepository, never()).update(updatedBadge);
    }
}
