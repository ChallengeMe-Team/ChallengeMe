package challengeme.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelul (entitatea) pentru Notificare.
 * Acesta reprezinta structura datelor pe care le vom stoca.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private UUID id;
    private UUID userId;
    private String message;
    private NotificationType type;
    private LocalDateTime timestamp;
    private boolean isRead;
}