package challengeme.backend.service;

import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.mapper.UserMapper;
import challengeme.backend.model.User;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
