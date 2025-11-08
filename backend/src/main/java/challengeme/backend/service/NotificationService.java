package challengeme.backend.service;

import challengeme.backend.dto.CreateNotificationDto;
import challengeme.backend.dto.UpdateNotificationDto;
import challengeme.backend.model.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interfata pentru Service.
 * Defineste logica de business si decupleaza Controller-ul de Repository.
 */
public interface NotificationService {

    List<Notification> getAllNotifications();
    Optional<Notification> getNotificationById(UUID id);
    List<Notification> getNotificationsByUserId(UUID userId);
    Notification createNotification(CreateNotificationDto dto);
    Optional<Notification> updateNotificationStatus(UUID id, UpdateNotificationDto dto);
    boolean deleteNotification(UUID id);

}