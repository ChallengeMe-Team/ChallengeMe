package challengeme.backend.service;

import challengeme.backend.model.User;
import challengeme.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        if (user.getId() == null)
            user.setId(UUID.randomUUID());
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.delete(id);
    }

    public User updateUser(UUID id, User user) {
        User existing = userRepository.findById(id); // va arunca excepție dacă nu există
        user.setId(existing.getId());
        userRepository.update(user);
        return user;
    }
}
