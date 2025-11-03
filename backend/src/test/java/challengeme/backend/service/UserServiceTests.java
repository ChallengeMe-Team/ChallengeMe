package challengeme.backend.service;

import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.model.User;
import challengeme.backend.repository.UserRepository;
import challengeme.backend.repository.inMemory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(
                new User("Ana", "ana@email.com", "pass123", 10),
                new User("Ion", "ion@email.com", "pass456", 5)
        );

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();
        assertEquals(users, result);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        User user = new User("Ana", "ana@email.com", "pass123", 10);
        when(userRepository.findById(user.getId())).thenReturn(user);

        User result = userService.getUserById(user.getId());
        assertEquals(user, result);

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testGetUserByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id))
                .thenThrow(new UserNotFoundException("User with id " + id + " not found"));

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testCreateUserWithId() {
        User user = new User(UUID.randomUUID(), "Ana", "ana@email.com", "pass123", 10);

        when(userRepository.save(user)).thenReturn(user);

        User created = userService.createUser(user);
        assertEquals(user, created);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateUserWithoutId() {
        User user = new User(null, "Ana", "ana@email.com", "pass123", 10);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser(user);
        assertNotNull(created.getId());
        verify(userRepository, times(1)).save(created);
    }

    @Test
    void testDeleteUser() {
        UUID id = UUID.randomUUID();

        doNothing().when(userRepository).delete(id);

        userService.deleteUser(id);
        verify(userRepository, times(1)).delete(id);
    }

    @Test
    void testDeleteUserNotFound() {
        UUID id = UUID.randomUUID();

        doThrow(new UserNotFoundException("User with id " + id + " not found"))
                .when(userRepository).delete(id);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));
        verify(userRepository, times(1)).delete(id);
    }

    @Test
    void testUpdateUser() {
        UUID id = UUID.randomUUID();
        User existingUser = new User(id, "Ana", "ana@email.com", "pass123", 10);
        User updatedUser = new User(id, "AnaUpdated", "anaupdated@email.com", "newpass123", 20);

        when(userRepository.findById(id)).thenReturn(existingUser);

        userService.updateUser(id, updatedUser);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).update(updatedUser);
    }

    @Test
    void testUpdateUserNotFound() {
        UUID id = UUID.randomUUID();
        User updatedUser = new User(id, "AnaUpdated", "anaupdated@email.com", "newpass123", 20);

        when(userRepository.findById(id))
                .thenThrow(new UserNotFoundException("User with id " + id + " not found"));

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, updatedUser));

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).update(updatedUser);
    }
}
