package challengeme.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * FIȘIER MODIFICAT
 * S-au adăugat validările @NotBlank și @NotNull conform cerințelor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private UUID id;

    @NotNull
    private UUID userId;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotNull(message = "NotificationType cannot be null")
    private NotificationType type;

    private LocalDateTime timestamp;

    private boolean isRead;
}