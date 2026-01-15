package challengeme.backend.mapper;

import challengeme.backend.dto.request.create.NotificationCreateRequest;
import challengeme.backend.dto.NotificationDTO;
import challengeme.backend.dto.request.update.NotificationUpdateRequest;
import challengeme.backend.model.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification entity) {
        return new NotificationDTO(
                entity.getId(),
                entity.getUserId(),
                entity.getMessage(),
                entity.getType(),
                entity.getTimestamp(),
                entity.isRead()
        );
    }

    public Notification toEntity(NotificationCreateRequest request) {
        Notification entity = new Notification();
        entity.setUserId(request.userId());
        entity.setMessage(request.message());
        entity.setType(request.type());
        entity.setTimestamp(java.time.ZonedDateTime.now(java.time.ZoneId.of("Europe/Bucharest")).toLocalDateTime());
        entity.setRead(false);
        return entity;
    }

    public void updateEntity(NotificationUpdateRequest request, Notification entity) {
        if (request.isRead() != null) {
            entity.setRead(request.isRead());
        }
    }
}
