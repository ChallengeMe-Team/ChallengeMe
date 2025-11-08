package challengeme.backend.dto;

import challengeme.backend.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO (Data Transfer Object) folosit pentru a primi datele
 * la crearea unei noi notificari.
 * Include validarile cerute.
 */
@Data
public class CreateNotificationDto {

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotBlank(message = "Message cannot be empty")
    private String message;

    @NotNull(message = "Type cannot be null")
    private NotificationType type;
}