package challengeme.backend.controller;

import challengeme.backend.dto.UserDTO;
import challengeme.backend.dto.request.create.UserCreateRequest;
import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.exception.GlobalExceptionHandler;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.mapper.UserMapper;
import challengeme.backend.model.User;
import challengeme.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Unit tests cu MockMvc – testează controller-ul izolat, folosind mock pentru UserService.

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTests {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetAllUsers() throws Exception {
        User u1 = new User(UUID.randomUUID(), "Ana", "ana@email.com", "pass123", 10);
        User u2 = new User(UUID.randomUUID(), "Ion", "ion@email.com", "pass456", 5);

        UserDTO dto1 = new UserDTO();
        dto1.setId(u1.getId()); dto1.setUsername(u1.getUsername()); dto1.setEmail(u1.getEmail()); dto1.setPoints(u1.getPoints());
        UserDTO dto2 = new UserDTO();
        dto2.setId(u2.getId()); dto2.setUsername(u2.getUsername()); dto2.setEmail(u2.getEmail()); dto2.setPoints(u2.getPoints());

        when(userService.getAllUsers()).thenReturn(List.of(u1, u2));
        when(mapper.toDTO(u1)).thenReturn(dto1);
        when(mapper.toDTO(u2)).thenReturn(dto2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("Ana"))
                .andExpect(jsonPath("$[1].username").value("Ion"));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Ana", "ana@email.com", "pass123", 10);
        UserDTO dto = new UserDTO();
        dto.setId(id); dto.setUsername("Ana"); dto.setEmail("ana@email.com"); dto.setPoints(10);

        when(userService.getUserById(id)).thenReturn(user);
        when(mapper.toDTO(user)).thenReturn(dto);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ana"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.getUserById(id)).thenThrow(new UserNotFoundException("User with id " + id + " not found"));

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id " + id + " not found"));
    }

    @Test
    void testCreateUser() throws Exception {
        UserCreateRequest req = new UserCreateRequest("Ana", "ana@email.com", "pass123");
        User created = new User(UUID.randomUUID(), req.getUsername(), req.getEmail(), req.getPassword(), 0);
        UserDTO dto = new UserDTO();
        dto.setId(created.getId()); dto.setUsername("Ana"); dto.setEmail("ana@email.com"); dto.setPoints(0);

        when(mapper.toEntity(req)).thenReturn(new User(null, req.getUsername(), req.getEmail(), req.getPassword(), 0));
        when(userService.createUser(any(User.class))).thenReturn(created);
        when(mapper.toDTO(created)).thenReturn(dto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Ana"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        UUID id = UUID.randomUUID();
        UserUpdateRequest req = new UserUpdateRequest("AnaUpdated","anaupdated@email.com","newpass",15);
        User updated = new User(id,"AnaUpdated","anaupdated@email.com","newpass",15);
        UserDTO dto = new UserDTO();
        dto.setId(id); dto.setUsername("AnaUpdated"); dto.setEmail("anaupdated@email.com"); dto.setPoints(15);

        when(userService.updateUser(eq(id), any(UserUpdateRequest.class))).thenReturn(updated);
        when(mapper.toDTO(updated)).thenReturn(dto);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("AnaUpdated"));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        UserUpdateRequest req = new UserUpdateRequest("AnaUpdated","anaupdated@email.com","newpass",15);
        when(userService.updateUser(eq(id), any(UserUpdateRequest.class)))
                .thenThrow(new UserNotFoundException("User with id " + id + " not found"));

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id " + id + " not found"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new UserNotFoundException("User with id " + id + " not found")).when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id " + id + " not found"));
    }
}
