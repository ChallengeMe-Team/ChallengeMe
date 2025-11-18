package challengeme.backend.controller;

import challengeme.backend.dto.UserBadgeDTO;
import challengeme.backend.dto.request.create.UserBadgeCreateRequest;
import challengeme.backend.dto.request.update.UserBadgeUpdateRequest;
import challengeme.backend.exception.UserBadgeNotFoundException;
import challengeme.backend.mapper.UserBadgeMapper;
import challengeme.backend.model.Badge;
import challengeme.backend.model.User;
import challengeme.backend.model.UserBadge;
import challengeme.backend.service.UserBadgeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Unit tests cu MockMvc – testează controller-ul izolat, folosind mock pentru UserBadgeService.

@WebMvcTest(UserBadgeController.class)
@ActiveProfiles("test")
@Import(challengeme.backend.config.TestSecurityConfig.class)
class UserBadgeControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserBadgeService service;

    @MockBean
    private UserBadgeMapper mapper;

    private UserBadgeDTO createDTO(UserBadge ub) {
        UserBadgeDTO dto = new UserBadgeDTO();
        dto.setId(ub.getId());
        dto.setUserId(ub.getUser() != null ? ub.getUser().getId() : UUID.randomUUID());
        dto.setBadgeId(ub.getBadge() != null ? ub.getBadge().getId() : UUID.randomUUID());
        dto.setDateAwarded(ub.getDateAwarded());
        return dto;
    }


    @Test
    void testGetAllUserBadges() throws Exception {
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com", "pass", 10);
        Badge badge = new Badge(UUID.randomUUID(), "Gold", "Top", "Complete 10");
        UserBadge ub1 = new UserBadge(UUID.randomUUID(), user, badge, LocalDate.now());
        UserBadge ub2 = new UserBadge(UUID.randomUUID(), user, badge, LocalDate.now());

        UserBadgeDTO dto1 = createDTO(ub1);
        UserBadgeDTO dto2 = createDTO(ub2);

        when(service.findAll()).thenReturn(List.of(ub1, ub2));
        when(mapper.toDTO(ub1)).thenReturn(dto1);
        when(mapper.toDTO(ub2)).thenReturn(dto2);

        mockMvc.perform(get("/api/userbadges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(service, times(1)).findAll();
    }


    @Test
    void testGetUserBadgeById_Success() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID badgeId = UUID.randomUUID();
        User user = new User(userId, "Ana", "ana@email.com", "pass", 10);
        Badge badge = new Badge(badgeId, "Gold", "Top", "Complete 10");
        UserBadge ub = new UserBadge(id, user, badge, LocalDate.now());

        UserBadgeDTO dto = createDTO(ub);

        when(service.findUserBadge(id)).thenReturn(ub);
        when(mapper.toDTO(ub)).thenReturn(dto);

        mockMvc.perform(get("/api/userbadges/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.badgeId").value(badgeId.toString()));

        verify(service, times(1)).findUserBadge(id);
    }


    @Test
    void testGetUserBadgeById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findUserBadge(id)).thenThrow(new UserBadgeNotFoundException("UserBadge with id " + id + " not found"));

        mockMvc.perform(get("/api/userbadges/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("UserBadge with id " + id + " not found"));

        verify(service, times(1)).findUserBadge(id);
    }

    @Test
    void testCreateUserBadge() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID badgeId = UUID.randomUUID();
        UUID createdId = UUID.randomUUID();

        UserBadgeCreateRequest req = new UserBadgeCreateRequest();
        req.setUserId(userId);
        req.setBadgeId(badgeId);

        UserBadge created = new UserBadge(createdId,
                new User(userId, "Ana", "ana@email.com", "pass", 10),
                new Badge(badgeId, "Gold", "Top", "Complete 10"),
                LocalDate.now()
        );

        UserBadgeDTO dto = createDTO(created);

        when(service.createUserBadge(userId, badgeId)).thenReturn(created);
        when(mapper.toDTO(created)).thenReturn(dto);

        mockMvc.perform(post("/api/userbadges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.badgeId").value(badgeId.toString()));

        verify(service, times(1)).createUserBadge(userId, badgeId);
    }

    @Test
    void testUpdateUserBadge() throws Exception {
        UUID id = UUID.randomUUID();
        LocalDate newDate = LocalDate.now().plusDays(1);

        UserBadgeUpdateRequest req = new UserBadgeUpdateRequest();
        req.setDateAwarded(newDate);

        UserBadge updated = new UserBadge(id,
                new User(UUID.randomUUID(), "Ana", "ana@email.com", "pass", 10),
                new Badge(UUID.randomUUID(), "Gold", "Top", "Complete 10"),
                newDate
        );

        UserBadgeDTO dto = createDTO(updated);

        when(service.updateUserBadge(eq(id), any(UserBadgeUpdateRequest.class))).thenReturn(updated);
        when(mapper.toDTO(updated)).thenReturn(dto);

        mockMvc.perform(put("/api/userbadges/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateAwarded").value(newDate.toString()));

        verify(service, times(1)).updateUserBadge(eq(id), any(UserBadgeUpdateRequest.class));
    }

    @Test
    void testDeleteUserBadge() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).deleteUserBadge(id);

        mockMvc.perform(delete("/api/userbadges/{id}", id))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteUserBadge(id);
    }

    @Test
    void testDeleteUserBadge_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new UserBadgeNotFoundException("Not found")).when(service).deleteUserBadge(id);

        mockMvc.perform(delete("/api/userbadges/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));

        verify(service, times(1)).deleteUserBadge(id);
    }
}