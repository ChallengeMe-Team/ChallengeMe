package challengeme.backend.repository;

import challengeme.backend.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, UUID> {
    List<UserBadge> findAllByUser_Username(String username);

    boolean existsByUserIdAndBadgeId(UUID userId, UUID badgeId);
}