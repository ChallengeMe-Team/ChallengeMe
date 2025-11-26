package challengeme.backend.controller;

import challengeme.backend.dto.request.create.BadgeCreateRequest;
import challengeme.backend.dto.request.update.BadgeUpdateRequest;
import challengeme.backend.dto.BadgeDTO;
import challengeme.backend.exception.BadgeNotFoundException;
import challengeme.backend.exception.GlobalExceptionHandler;
import challengeme.backend.mapper.BadgeMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Unit tests cu MockMvc – testează controller-ul izolat, folosind mock pentru BadgeService.

@ExtendWith(MockitoExtension.class)
public class BadgeControllerUnitTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BadgeService badgeService;

    @Mock
    private BadgeMapper badgeMapper;

    @InjectMocks
    private BadgeController badgeController;

    private UUID badgeId;
    private Badge badge;
    private BadgeDTO badgeDTO;

    @BeforeEach
    void setup() {
        badgeId = UUID.randomUUID();
        badge = new Badge(badgeId, "Explorer", "Visited 5 locations", "Visit 5 locations");
        badgeDTO = new BadgeDTO(badgeId, badge.getName(), badge.getDescription(), badge.getCriteria());

        mockMvc = MockMvcBuilders.standaloneSetup(badgeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // --- GET ALL ---
    @Test
    void testGetAllBadges() throws Exception {
        when(badgeService.getAllBadges()).thenReturn(List.of(badge));
        when(badgeMapper.toDTO(badge)).thenReturn(badgeDTO);

        mockMvc.perform(get("/api/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Explorer"));

        verify(badgeService).getAllBadges();
    }

    // --- GET BY ID ---
    @Test
    void testGetBadgeById_Success() throws Exception {
        when(badgeService.getBadgeById(badgeId)).thenReturn(badge);
        when(badgeMapper.toDTO(badge)).thenReturn(badgeDTO);

        mockMvc.perform(get("/api/badges/{id}", badgeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Explorer"));

        verify(badgeService).getBadgeById(badgeId);
    }

    @Test
    void testGetBadgeById_NotFound() throws Exception {
        when(badgeService.getBadgeById(badgeId))
                .thenThrow(new BadgeNotFoundException("Badge with id " + badgeId + " not found"));

        mockMvc.perform(get("/api/badges/{id}", badgeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Badge with id " + badgeId + " not found"));

        verify(badgeService).getBadgeById(badgeId);
    }

    // --- CREATE ---
    @Test
    void testCreateBadge() throws Exception {
        BadgeCreateRequest request = new BadgeCreateRequest("Explorer", "Visited 5 locations", "Visit 5 locations");

        when(badgeMapper.toEntity(any(BadgeCreateRequest.class))).thenReturn(badge);
        when(badgeService.createBadge(badge)).thenReturn(badge);
        when(badgeMapper.toDTO(badge)).thenReturn(badgeDTO);

        mockMvc.perform(post("/api/badges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Explorer"));

        verify(badgeService).createBadge(badge);
    }

    // --- UPDATE ---
    @Test
    void testUpdateBadge() throws Exception {
        BadgeUpdateRequest request = new BadgeUpdateRequest("Explorer Updated", "Visited 10 locations", "Visit 10 locations");
        Badge updatedBadge = new Badge(badgeId, request.name(), request.description(), request.criteria());
        BadgeDTO updatedDTO = new BadgeDTO(badgeId, request.name(), request.description(), request.criteria());

        when(badgeService.updateBadge(eq(badgeId), any(BadgeUpdateRequest.class))).thenReturn(updatedBadge);
        when(badgeMapper.toDTO(updatedBadge)).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/badges/{id}", badgeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Explorer Updated"));

        verify(badgeService).updateBadge(eq(badgeId), any(BadgeUpdateRequest.class));
    }

    // --- DELETE ---
    @Test
    void testDeleteBadge_Success() throws Exception {
        doNothing().when(badgeService).deleteBadge(badgeId);

        mockMvc.perform(delete("/api/badges/{id}", badgeId))
                .andExpect(status().isNoContent());

        verify(badgeService).deleteBadge(badgeId);
    }

    @Test
    void testDeleteBadge_NotFound() throws Exception {
        doThrow(new BadgeNotFoundException("Badge with id " + badgeId + " not found"))
                .when(badgeService).deleteBadge(badgeId);

        mockMvc.perform(delete("/api/badges/{id}", badgeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Badge with id " + badgeId + " not found"));

        verify(badgeService).deleteBadge(badgeId);
    }
}
