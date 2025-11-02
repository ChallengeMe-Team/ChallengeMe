package challengeme.backend.service;

import challengeme.backend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserBadgeServiceTests {

    private RepositoryUserBadge repositoryUserBadge;
    private UserBadgeService userBadgeService;

    @BeforeEach
    void setup() {
        repositoryUserBadge = mock(RepositoryUserBadge.class);
        userBadgeService = new UserBadgeService(repositoryUserBadge);
    }

    @Test
    void testFindAll() {
        when(repositoryUserBadge.getAll()).thenReturn(List.of());
        userBadgeService.findAll();
        verify(repositoryUserBadge, times(1)).getAll();
    }

    @Test
    void testFindUserBadge() {
        UUID id = UUID.randomUUID();
        when(repositoryUserBadge.getUserBadge(id)).thenReturn(new UserBadge());
        userBadgeService.findUserBadge(id);
        verify(repositoryUserBadge, times(1)).getUserBadge(id);
    }

    @Test
    void testCreateUserBadgeAssignsId() {
        UserBadge ub = new UserBadge(null, new User(), new Badge(), LocalDate.now());
        when(repositoryUserBadge.create(any())).thenReturn(ub);

        UserBadge result = userBadgeService.createUserBadge(ub);
        assertNotNull(result);
        verify(repositoryUserBadge).create(any());
    }

    @Test
    void testDeleteUserBadge() {
        UUID id = UUID.randomUUID();
        userBadgeService.deleteUserBadge(id);
        verify(repositoryUserBadge).delete(id);
    }

    @Test
    void testUpdateUserBadge() {
        UUID id = UUID.randomUUID();
        UserBadge ub = new UserBadge(id, new User(), new Badge(), LocalDate.now());
        userBadgeService.updateUserBadge(id, ub);
        verify(repositoryUserBadge).update(ub);
    }
}
