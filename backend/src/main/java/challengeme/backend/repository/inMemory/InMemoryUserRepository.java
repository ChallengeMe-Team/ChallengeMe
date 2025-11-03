package challengeme.backend.repository.inMemory;

import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.model.User;
import challengeme.backend.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final List<User> users = new ArrayList<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public User findById(UUID id) {
        return users.stream().filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public User save(User user) {
        users.add(user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        if (!removed) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
    }

    @Override
    public void update(User user) {
        users.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .map(u -> {
                    u.setUsername(user.getUsername());
                    u.setEmail(user.getEmail());
                    u.setPassword(user.getPassword());
                    u.setPoints(user.getPoints());
                    return u;
                })
                .orElseThrow(() -> new UserNotFoundException("User with id " + user.getId() + " not found"));
    }

}
