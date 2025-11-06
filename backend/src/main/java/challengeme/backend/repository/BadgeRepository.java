package challengeme.backend.repository;

import challengeme.backend.model.Badge;

import java.util.List;
import java.util.UUID;

public interface BadgeRepository {

    List<Badge> findAll();

    Badge findById(UUID id);

    Badge save(Badge badge);

    void delete(UUID id);

    void update(Badge badge);

}
