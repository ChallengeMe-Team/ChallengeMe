package challengeme.backend.dto;

import challengeme.backend.model.NotificationType;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDTO(
        UUID id,
        UUID userId,
        String message,
        NotificationType type,
        LocalDateTime timestamp,
        boolean isRead
) {}
