package challengeme.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an automated alert or message sent to a user.
 * Tracks system events, challenge assignments, and achievement unlocks.
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;

    /** The recipient of the notification. */
    @NotNull
    private UUID userId;

    /** The human-readable content of the alert. */
    @NotBlank(message = "Message cannot be blank")
    private String message;

    /** Categorization of the alert (CHALLENGE, BADGE, SYSTEM). */
    @NotNull(message = "NotificationType cannot be null")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    /** The exact moment the notification was generated. */
    private LocalDateTime timestamp;

    /** Tracking flag to distinguish between new and seen alerts. */
    private boolean isRead;

}