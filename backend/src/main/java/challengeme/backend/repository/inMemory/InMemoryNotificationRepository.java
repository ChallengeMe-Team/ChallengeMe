package challengeme.backend.repository.inMemory;

import challengeme.backend.model.Notification;
import challengeme.backend.repository.NotificationRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * FIȘIER MODIFICAT
 * Implementarea in-memory a repository-ului pentru notificări.
 * Folosește un ArrayList standard, protejat de blocuri 'synchronized'
 * pentru a fi thread-safe.
 */
@Repository("inMemoryNotificationRepository")
public class InMemoryNotificationRepository implements NotificationRepository {

    /**
     * S-a schimbat din CopyOnWriteArrayList în ArrayList simplu.
     * Accesul la această listă trebuie acum să fie sincronizat.
     */
    private final List<Notification> database = new ArrayList<>();

    @Override
    public Notification save(Notification notification) {
        // Blocam lista pe durata scrierii
        synchronized (database) {
            database.add(notification);
            return notification;
        }
    }

    @Override
    public List<Notification> findAll() {
        // Blocam lista pe durata citirii pentru a crea o copie sigură
        synchronized (database) {
            // Returnăm o copie a listei, nu lista originală
            return new ArrayList<>(database);
        }
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        // Blocam lista pe durata citirii/căutării
        synchronized (database) {
            return database.stream()
                    .filter(notification -> notification.getId().equals(id))
                    .findFirst();
        }
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        // Blocam lista pe durata citirii/filtrării
        synchronized (database) {
            return database.stream()
                    .filter(notification -> notification.getUserId().equals(userId))
                    .collect(Collectors.toList()); // .toList() creează o listă nouă, sigură
        }
    }

    @Override
    public void deleteById(UUID id) {
        // Blocam lista pe durata ștergerii
        synchronized (database) {
            database.removeIf(notification -> notification.getId().equals(id));
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }
}