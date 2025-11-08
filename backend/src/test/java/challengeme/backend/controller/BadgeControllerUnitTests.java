package challengeme.backend.controller;

import challengeme.backend.exception.GlobalExceptionHandler;
import challengeme.backend.exception.BadgeNotFoundException;
import challengeme.backend.model.Badge;
import challengeme.backend.service.BadgeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BadgeControllerUnitTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BadgeService badgeService;

    @InjectMocks
    private BadgeController badgeController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(badgeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // GET /api/badges
    @Test
    void testGetAllBadges() throws Exception {
        Badge badge1 = new Badge(UUID.randomUUID(), "Explorer", "Visited 5 locations", "Visit 5 locations");
        Badge badge2 = new Badge(UUID.randomUUID(), "Achiever", "Completed all tasks", "Complete all challenges");

        when(badgeService.getAllBadges()).thenReturn(List.of(badge1, badge2));

        mockMvc.perform(get("/api/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Explorer"))
                .andExpect(jsonPath("$[1].name").value("Achiever"));

        verify(badgeService, times(1)).getAllBadges();
    }

    // GET /api/badges/{id}
    @Test
    void testGetBadgeById() throws Exception {
        UUID id = UUID.randomUUID();
        Badge badge = new Badge(id, "Explorer", "Visited 5 locations", "Visit 5 locations");
        when(badgeService.getBadgeById(id)).thenReturn(badge);

        mockMvc.perform(get("/api/badges/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Explorer"));

        verify(badgeService, times(1)).getBadgeById(id);
    }

    @Test
    void testGetBadgeByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(badgeService.getBadgeById(id))
                .thenThrow(new BadgeNotFoundException("Badge with id " + id + " not found"));

        mockMvc.perform(get("/api/badges/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Badge with id " + id + " not found"));

        verify(badgeService, times(1)).getBadgeById(id);
    }

    // POST /api/badges
    @Test
    void testCreateBadge() throws Exception {
        Badge badge = new Badge(null, "Explorer", "Visited 5 locations", "Visit 5 locations");
        Badge created = new Badge(UUID.randomUUID(), badge.getName(), badge.getDescription(), badge.getCriteria());

        when(badgeService.createBadge(any(Badge.class))).thenReturn(created);

        mockMvc.perform(post("/api/badges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badge)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Explorer"));

        verify(badgeService, times(1)).createBadge(any(Badge.class));
    }

    // PUT /api/badges/{id}
    @Test
    void testUpdateBadge() throws Exception {
        UUID id = UUID.randomUUID();
        Badge badge = new Badge(null, "Explorer Updated", "Visited 10 locations", "Visit 10 locations");
        Badge updated = new Badge(id, badge.getName(), badge.getDescription(), badge.getCriteria());

        when(badgeService.updateBadge(eq(id), any(Badge.class))).thenReturn(updated);

        mockMvc.perform(put("/api/badges/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Explorer Updated"));

        verify(badgeService, times(1)).updateBadge(eq(id), any(Badge.class));
    }

    // DELETE /api/badges/{id}
    @Test
    void testDeleteBadge() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(badgeService).deleteBadge(id);

        mockMvc.perform(delete("/api/badges/{id}", id))
                .andExpect(status().isNoContent());

        verify(badgeService, times(1)).deleteBadge(id);
    }

    @Test
    void testDeleteBadgeNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new BadgeNotFoundException("Badge with id " + id + " not found"))
                .when(badgeService).deleteBadge(id);

        mockMvc.perform(delete("/api/badges/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Badge with id " + id + " not found"));

        verify(badgeService, times(1)).deleteBadge(id);
    }
}
