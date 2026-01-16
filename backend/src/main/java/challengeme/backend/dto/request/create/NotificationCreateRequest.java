package challengeme.backend.dto.request.create;

import challengeme.backend.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Request DTO for triggering a system notification.
 * Used across services to alert users about new assignments or social interactions.
 * * @param userId The recipient of the notification.
 * @param message The text content to be displayed in the notification.
 * @param type The category of the alert (e.g., CHALLENGE, SYSTEM).
 */
public record NotificationCreateRequest(
        @NotNull UUID userId,
        @NotBlank String message,
        @NotNull NotificationType type
) {}
