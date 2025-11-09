package challengeme.backend.repository;

import challengeme.backend.model.UserBadge;

import java.util.List;
import java.util.UUID;

public interface RepositoryUserBadge {
    List<UserBadge> getAll();
    UserBadge getUserBadge(UUID id);
    UserBadge create(UserBadge userBadge);
    void delete(UUID id);
    void update(UserBadge userBadge);
}
