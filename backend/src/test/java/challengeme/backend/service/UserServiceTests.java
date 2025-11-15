package challengeme.backend.service;

import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.mapper.UserMapper;
import challengeme.backend.model.User;
import challengeme.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UUID userId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
    }

    // --- CREATE ---

    @Test
    void testCreateUser() {
        User user = new User(userId, "Ana", "ana@email.com", "pass123", 10);
        when(userRepository.save(user)).thenReturn(user);

        User created = userService.createUser(user);

        assertEquals(user, created);
        verify(userRepository).save(user);
    }

    // --- READ ---

    @Test
    void testGetAllUsers() {
        User u1 = new User(UUID.randomUUID(), "Ana", "ana@email.com", "pass123", 10);
        User u2 = new User(UUID.randomUUID(), "Ion", "ion@email.com", "pass456", 5);

        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertTrue(result.contains(u1));
        assertTrue(result.contains(u2));
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserById_Success() {
        User user = new User(userId, "Ana", "ana@email.com", "pass123", 10);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertEquals(user, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    // --- UPDATE ---

    @Test
    void testUpdateUser_Success() {
        User existing = new User(userId, "Ana", "ana@email.com", "pass123", 10);
        UserUpdateRequest request = new UserUpdateRequest("AnaUpdated", "new@email.com", "newpass", 20);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User updated = userService.updateUser(userId, request);

        verify(userMapper).updateEntity(request, existing);
        verify(userRepository).save(existing);
        assertEquals(existing, updated);
    }

    @Test
    void testUpdateUser_NotFound() {
        UserUpdateRequest request = new UserUpdateRequest("AnaUpdated", null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, request));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).updateEntity(any(), any());
    }

    // --- DELETE ---

    @Test
    void testDeleteUser_Success() {
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository).deleteById(userId);
    }
}
