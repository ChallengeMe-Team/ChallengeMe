package challengeme.backend.service;

import challengeme.backend.dto.FriendDTO;
import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.mapper.UserMapper;
import challengeme.backend.model.User;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import challengeme.backend.dto.request.update.ChangePasswordRequest;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    private final PasswordEncoder passwordEncoder;

    // -----------------------------------------------------------
    // BASIC CRUD
    // -----------------------------------------------------------
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    // Update User with Uniqueness Check
    public User updateUser(UUID id, UserUpdateRequest request) {
        User user = getUserById(id);

        // 1. Validare Unicitate USERNAME
        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new RuntimeException("This username is already taken. Please choose another.");
            }
        }

        // 2. Validare Unicitate EMAIL
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new RuntimeException("This email address is already in use.");
            }
        }

        mapper.updateEntity(request, user);
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = getUserById(userId);

        // Verific daca parola curenta (raw) se potriveste cu cea din baza de date (hash)
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect current password");
        }

        // Criptez noua parola
        String newHash = passwordEncoder.encode(request.newPassword());
        user.setPassword(newHash);

        userRepository.save(user);
    }

    // -----------------------------------------------------------
    // SEARCH USER BY USERNAME
    // -----------------------------------------------------------
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // -----------------------------------------------------------
    // GET FRIENDS OF USER
    // -----------------------------------------------------------
    public List<FriendDTO> getUserFriends(UUID currentUserId) {
        User currentUser = getUserById(currentUserId);

        List<UUID> friendIds = currentUser.getFriendIds();
        if (friendIds == null || friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> friends = userRepository.findAllById(friendIds);

        return friends.stream()
                .map(mapper::toFriendDTO)
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------
    // ADD FRIEND LOGIC
    // -----------------------------------------------------------
    @Transactional
    public void addFriend(UUID currentUserId, String friendUsername) {

        User current = getUserById(currentUserId);

        // Rule: cannot add yourself
        if (current.getUsername().equalsIgnoreCase(friendUsername)) {
            throw new RuntimeException("You cannot add yourself");
        }

        // Find target friend
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Rule: cannot add existing friend
        if (current.getFriendIds().contains(friend.getId())) {
            throw new RuntimeException("User is already your friend");
        }

        // Add
        current.getFriendIds().add(friend.getId());
        userRepository.save(current);
    }
}
