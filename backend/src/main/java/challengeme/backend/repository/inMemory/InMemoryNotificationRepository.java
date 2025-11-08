package challengeme.backend.repository.inMemory;

import challengeme.backend.model.Notification;
import challengeme.backend.repository.NotificationRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementarea in-memory a repository-ului.
 * Foloseste un ConcurrentHashMap pentru a stoca notificarile.
 */
@Repository("inMemoryNotificationRepository")
public class InMemoryNotificationRepository implements NotificationRepository {

    private final Map<UUID, Notification> database = new ConcurrentHashMap<>();

    @Override
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            // Este o notificare noua (Create)
            notification.setId(UUID.randomUUID());
            notification.setTimestamp(LocalDateTime.now());
            notification.setRead(false); // Default la creare
        } else {
            // Este o actualizare (Update)
            // Actualizam doar timestamp-ul
            notification.setTimestamp(LocalDateTime.now());
        }

        database.put(notification.getId(), notification);
        return notification;
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<Notification> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        return database.values().stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        database.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return database.containsKey(id);
    }
}