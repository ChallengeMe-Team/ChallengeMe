package challengeme.backend.service;

import challengeme.backend.model.UserBadge;
import challengeme.backend.repository.RepositoryUserBadge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserBadgeService {

    private final RepositoryUserBadge repositoryUserBadge;
    @Autowired
    public UserBadgeService(RepositoryUserBadge repositoryUserBadge) {
        this.repositoryUserBadge = repositoryUserBadge;
    }

    public List<UserBadge> findAll() {
        return repositoryUserBadge.getAll();
    }

    public UserBadge findUserBadge(UUID id) {
        return repositoryUserBadge.getUserBadge(id);
    }

    public UserBadge createUserBadge(UserBadge userBadge) {
        if (userBadge.getId() == null) {
            userBadge.setId(UUID.randomUUID());
        }
        return repositoryUserBadge.create(userBadge);
    }

    public void deleteUserBadge(UUID id) {
        repositoryUserBadge.delete(id);
    }

    public UserBadge updateUserBadge(UUID id, UserBadge userBadge) {
        userBadge.setId(id);
        repositoryUserBadge.update(userBadge);
        return userBadge;
    }
}
