package challengeme.backend.controller;

import challengeme.backend.exception.GlobalExceptionHandler;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.model.User;
import challengeme.backend.service.UserService;
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

//Unit tests cu MockMvc – testează controller-ul izolat, folosind mock pentru UserService.

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTests {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        // Crează MockMvc standalone, fără Spring context complet
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // GET /api/users
    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User("Ana", "ana@email.com", "pass123", 10);
        User user2 = new User("Ion", "ion@email.com", "pass456", 5);

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("Ana"))
                .andExpect(jsonPath("$[1].username").value("Ion"));

        verify(userService, times(1)).getAllUsers();
    }

    // GET /api/users/{id}
    @Test
    void testGetUserById() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Ana", "ana@email.com", "pass123", 10);
        when(userService.getUserById(id)).thenReturn(user);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ana"));

        verify(userService, times(1)).getUserById(id);
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.getUserById(id)).thenThrow(new UserNotFoundException("User with id " + id + " not found"));

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id " + id + " not found"));

        verify(userService, times(1)).getUserById(id);
    }

    // POST /api/users
    @Test
    void testCreateUser() throws Exception {
        User user = new User(null, "Ana", "ana@email.com", "pass123", 10);
        User created = new User(UUID.randomUUID(), user.getUsername(), user.getEmail(), user.getPassword(), user.getPoints());

        when(userService.createUser(any(User.class))).thenReturn(created);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Ana"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    // PUT /api/users/{id}
    @Test
    void testUpdateUser() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User(null, "AnaUpdated", "anaupdated@email.com", "newpass", 15);
        User updated = new User(id, user.getUsername(), user.getEmail(), user.getPassword(), user.getPoints());

        when(userService.updateUser(eq(id), any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("AnaUpdated"));

        verify(userService, times(1)).updateUser(eq(id), any(User.class));
    }

    // DELETE /api/users/{id}
    @Test
    void testDeleteUser() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(id);
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new UserNotFoundException("User with id " + id + " not found"))
                .when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id " + id + " not found"));

        verify(userService, times(1)).deleteUser(id);
    }
}
