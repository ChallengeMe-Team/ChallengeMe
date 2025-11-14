package challengeme.backend.repository;

import challengeme.backend.model.Notification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interfata pentru Repository.
 * Defineste contractul pentru operatiile de persistenta a datelor.
 */
@Repository
public interface NotificationRepository {

    Notification save(Notification notification);
    Optional<Notification> findById(UUID id);
    List<Notification> findAll();
    List<Notification> findByUserId(UUID userId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}