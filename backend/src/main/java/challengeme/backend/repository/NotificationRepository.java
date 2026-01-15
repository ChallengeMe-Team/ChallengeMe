package challengeme.backend.repository;

import challengeme.backend.model.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserId(UUID userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE notifications SET is_read = true WHERE user_id = :userId AND is_read = false", nativeQuery = true)
    void markAllAsReadNative(@Param("userId") UUID userId);
}
