package challengeme.backend.service;

import challengeme.backend.dto.request.update.BadgeUpdateRequest;
import challengeme.backend.exception.BadgeNotFoundException;
import challengeme.backend.mapper.BadgeMapper;
import challengeme.backend.model.Badge;
import challengeme.backend.repository.BadgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BadgeServiceTests {

    private BadgeRepository badgeRepository;
    private BadgeMapper badgeMapper;
    private BadgeService badgeService;

    @BeforeEach
    void setup() {
        badgeRepository = mock(BadgeRepository.class);
        badgeMapper = mock(BadgeMapper.class);
        badgeService = new BadgeService(badgeRepository, badgeMapper);
    }

    @Test
    void testGetAllBadges() {
        List<Badge> badges = Arrays.asList(
                new Badge(UUID.randomUUID(), "Explorer", "Desc1", "Criteria1"),
                new Badge(UUID.randomUUID(), "Achiever", "Desc2", "Criteria2")
        );

        when(badgeRepository.findAll()).thenReturn(badges);

        List<Badge> result = badgeService.getAllBadges();
        assertEquals(badges, result);

        verify(badgeRepository, times(1)).findAll();
    }

    @Test
    void testGetBadgeByIdExists() {
        Badge badge = new Badge(UUID.randomUUID(), "Explorer", "Desc", "Criteria");
        when(badgeRepository.findById(badge.getId())).thenReturn(Optional.of(badge));

        Badge result = badgeService.getBadgeById(badge.getId());
        assertEquals(badge, result);

        verify(badgeRepository, times(1)).findById(badge.getId());
    }

    @Test
    void testGetBadgeByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(badgeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BadgeNotFoundException.class, () -> badgeService.getBadgeById(id));

        verify(badgeRepository, times(1)).findById(id);
    }

    @Test
    void testCreateBadge() {
        Badge badge = new Badge(null, "New", "Desc", "Criteria");

        when(badgeRepository.save(badge)).thenAnswer(invocation -> {
            Badge b = invocation.getArgument(0);
            b.setId(UUID.randomUUID());
            return b;
        });

        Badge created = badgeService.createBadge(badge);
        assertNotNull(created.getId());
        assertEquals("New", created.getName());

        verify(badgeRepository, times(1)).save(badge);
    }

    @Test
    void testUpdateBadgeExists() {
        UUID id = UUID.randomUUID();
        Badge existing = new Badge(id, "Old", "DescOld", "CriteriaOld");

        BadgeUpdateRequest updateRequest = new BadgeUpdateRequest("Updated", "DescUpdated", "CriteriaUpdated");

        when(badgeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(badgeRepository.save(existing)).thenReturn(existing); // save returnează entity-ul actualizat

        Badge updated = badgeService.updateBadge(id, updateRequest);

        verify(badgeMapper, times(1)).updateEntity(updateRequest, existing);

        assertEquals(id, updated.getId());
    }

    @Test
    void testUpdateBadgeNotFound() {
        UUID id = UUID.randomUUID();
        BadgeUpdateRequest request = new BadgeUpdateRequest("NonExistent", "Desc", "Criteria");

        when(badgeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BadgeNotFoundException.class, () -> badgeService.updateBadge(id, request));

        verify(badgeMapper, never()).updateEntity(any(), any());
        verify(badgeRepository, never()).save(any());
    }

    @Test
    void testDeleteBadgeExists() {
        UUID id = UUID.randomUUID();
        Badge existing = new Badge(id, "Test", "Desc", "Criteria");

        when(badgeRepository.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(badgeRepository).delete(existing);

        badgeService.deleteBadge(id);

        verify(badgeRepository, times(1)).delete(existing);
        verify(badgeRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteBadgeNotFound() {
        UUID id = UUID.randomUUID();
        when(badgeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BadgeNotFoundException.class, () -> badgeService.deleteBadge(id));

        verify(badgeRepository, never()).delete(any());
    }
}
