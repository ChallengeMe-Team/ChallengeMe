package challengeme.backend.service;

import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.mapper.UserMapper;
import challengeme.backend.model.User;
import challengeme.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder; // Import added

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    // You added these dependencies in UserService, so they must be mocked here
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ChallengeService challengeService;

    @InjectMocks
    private UserService userService;

    private UUID userId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
    }

    // Helper method to create a User safely using setters
    private User createMockUser(UUID id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password123");
        // Initialize lists to avoid NullPointerException if tested
        user.setFriendIds(new ArrayList<>());
        return user;
    }

    // --- CREATE ---

    @Test
    void testCreateUser() {
        // Fix: Use helper or setters instead of complex constructor
        User user = createMockUser(userId, "Ana", "ana@test.com");

        when(userRepository.save(user)).thenReturn(user);

        User created = userService.createUser(user);

        assertEquals(user, created);
        verify(userRepository).save(user);
    }

    // --- READ ---

    @Test
    void testGetAllUsers() {
        User u1 = createMockUser(UUID.randomUUID(), "Ana", "ana@email.com");
        User u2 = createMockUser(UUID.randomUUID(), "Ion", "ion@email.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserById_Success() {
        User user = createMockUser(userId, "Ana", "ana@email.com");
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
        User existing = createMockUser(userId, "Ana", "ana@email.com");

        // FIX: The UserService only looks for username, email, and avatar.
        // We match the record signature: (String username, String email, String avatar)
        // Adjust these nulls/strings if your Record definition is different.
        UserUpdateRequest request = new UserUpdateRequest("AnaUpdated", "new@email.com", "dummyPass", 0, "new_avatar_url");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        // Ensure username checks pass
        when(userRepository.existsByUsername("AnaUpdated")).thenReturn(false);
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);

        User updated = userService.updateUser(userId, request);

        // Verification
        assertEquals("AnaUpdated", updated.getUsername());
        assertEquals("new@email.com", updated.getEmail());

        // Check that synchronizeUsername was called because username changed
        verify(challengeService).synchronizeUsername("Ana", "AnaUpdated");
        verify(userRepository).save(existing);
    }

    @Test
    void testUpdateUser_NotFound() {
        UserUpdateRequest request = new UserUpdateRequest("AnaUpdated", "email", "pass", 0, "avatar");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, request));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    // --- DELETE ---

    @Test
    void testDeleteUser_Success() {
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository).deleteById(userId);
    }
}