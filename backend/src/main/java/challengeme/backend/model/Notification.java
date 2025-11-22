package challengeme.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private UUID userId;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotNull(message = "NotificationType cannot be null")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private LocalDateTime timestamp;

    private boolean isRead;

}