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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

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

    public User updateUser(UUID id, UserUpdateRequest request) {
        User user = getUserById(id);
        mapper.updateEntity(request, user);
        return userRepository.save(user);
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
