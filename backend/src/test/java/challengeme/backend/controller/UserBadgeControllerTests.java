package challengeme.backend.controller;

import challengeme.backend.exception.EntityNotFoundException;
import challengeme.backend.model.Badge;
import challengeme.backend.model.User;
import challengeme.backend.model.UserBadge;
import challengeme.backend.service.UserBadgeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserBadgeController.class)
public class UserBadgeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserBadgeService userBadgeService;

    @Test
    void testFindAll() throws Exception {
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com", "secret123", 10);
        Badge badge = new Badge(UUID.randomUUID(), "Gold", "Top performer", "Complete 10 challenges");

        UserBadge ub1 = new UserBadge(UUID.randomUUID(), user, badge, LocalDate.now());
        UserBadge ub2 = new UserBadge(UUID.randomUUID(), user, badge, LocalDate.now());

        when(userBadgeService.findAll()).thenReturn(List.of(ub1, ub2));

        mockMvc.perform(get("/userbadges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(userBadgeService, times(1)).findAll();
    }

    @Test
    void testFindUserBadgeById() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com", "secret123", 10);
        Badge badge = new Badge(UUID.randomUUID(), "Gold", "Top performer", "Complete 10 challenges");
        UserBadge userBadge = new UserBadge(id, user, badge, LocalDate.now());

        when(userBadgeService.findUserBadge(id)).thenReturn(userBadge);

        mockMvc.perform(get("/userbadges/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("Ana"))
                .andExpect(jsonPath("$.badge.name").value("Gold"))
                .andExpect(jsonPath("$.badge.criteria").value("Complete 10 challenges"));
    }

    @Test
    void testFindUserBadgeNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userBadgeService.findUserBadge(id))
                .thenThrow(new EntityNotFoundException("UserBadge with id " + id + " not found"));

        mockMvc.perform(get("/userbadges/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("UserBadge with id " + id + " not found"));
    }

    @Test
    void testCreateUserBadge() throws Exception {
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com", "secret123", 10);
        Badge badge = new Badge(UUID.randomUUID(), "Gold", "Top performer", "Complete 10 challenges");
        UserBadge ub = new UserBadge(null, user, badge, LocalDate.now());

        mockMvc.perform(post("/userbadges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ub)))
                .andExpect(status().isCreated());

        verify(userBadgeService, times(1)).createUserBadge(any(UserBadge.class));
    }

    @Test
    void testUpdateUserBadge() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com", "secret123", 10);
        Badge badge = new Badge(UUID.randomUUID(), "Silver", "Updated badge", "Achieve 20 challenges");
        UserBadge ub = new UserBadge(id, user, badge, LocalDate.now());

        mockMvc.perform(put("/userbadges/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ub)))
                .andExpect(status().isOk());

        verify(userBadgeService, times(1)).updateUserBadge(eq(id), any(UserBadge.class));
    }

    @Test
    void testDeleteUserBadge() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/userbadges/{id}", id))
                .andExpect(status().isNoContent());

        verify(userBadgeService, times(1)).deleteUserBadge(id);
    }
}
