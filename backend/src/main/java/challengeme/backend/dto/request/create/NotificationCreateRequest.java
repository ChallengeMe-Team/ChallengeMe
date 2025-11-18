package challengeme.backend.dto.request.create;

import challengeme.backend.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record NotificationCreateRequest(
        @NotNull UUID userId,
        @NotBlank String message,
        @NotNull NotificationType type
) {}
