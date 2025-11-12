package challengeme.backend.repository.inMemory;

import challengeme.backend.model.Notification;
import challengeme.backend.repository.NotificationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * FIȘIER MODIFICAT MASIV
 * S-a refactorizat complet pentru a folosi List în loc de Map,
 * conform cerințelor din review.
 */
@Repository
public class InMemoryNotificationRepository implements NotificationRepository {

    // S-a schimbat din Map în List, conform cerințelor
    private final List<Notification> database = new CopyOnWriteArrayList<>();

    @Override
    public Notification save(Notification notification) {
        // Logica de salvare s-a schimbat:
        // Verificăm dacă există deja pentru a face update, altfel adăugăm

        // Căutăm un index
        int index = -1;
        for (int i = 0; i < database.size(); i++) {
            if (database.get(i).getId().equals(notification.getId())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            // Update: înlocuim elementul de la index
            database.set(index, notification);
        } else {
            // Create: adăugăm elementul nou
            database.add(notification);
        }
        return notification;
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        // Căutare prin stream în listă
        return database.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Notification> findAll() {
        // Returnăm o copie a listei
        return List.copyOf(database);
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        // Căutare prin stream și filtrare
        return database.stream()
                .filter(n -> n.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        // Ștergere folosind removeIf, care e thread-safe pentru CopyOnWriteArrayList
        database.removeIf(n -> n.getId().equals(id));
    }

    @Override
    public boolean existsById(UUID id) {
        // Căutare prin stream
        return database.stream()
                .anyMatch(n -> n.getId().equals(id));
    }
}