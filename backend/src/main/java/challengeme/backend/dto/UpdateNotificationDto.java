package challengeme.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO simplu pentru actualizarea starii 'isRead' a unei notificari.
 */
@Data
public class UpdateNotificationDto {

    @NotNull(message = "isRead status cannot be null")
    private Boolean isRead;
}