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


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id" + id));
    }

    public List<FriendDTO> getUserFriends(UUID currentUserId) {
        // 1. Găsim userul curent
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UUID> friendIds = currentUser.getFriendIds();

        if (friendIds == null || friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Căutăm toți prietenii după lista de ID-uri
        List<User> friends = userRepository.findAllById(friendIds);

        // 3. Convertim în DTO
        return friends.stream()
                .map(u -> new FriendDTO(u.getId(), u.getUsername(), u.getPoints()))
                .collect(Collectors.toList());
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public User updateUser(UUID id, UserUpdateRequest request) {
        User user = getUserById(id); // aruncă UserNotFoundException dacă nu există

        mapper.updateEntity(request, user); // aplică update doar pe câmpurile trimise

        return userRepository.save(user); // salvează și returnează
    }
}
