package challengeme.backend.repository;

import challengeme.backend.model.User;

import java.util.*;

public interface UserRepository {

    List<User> findAll();

    User findById(UUID id);

    User save(User user);

    void delete(UUID id);

    void update(User user);

}
