package challengeme.backend.service;

import challengeme.backend.exception.NotificationNotFoundException;
import challengeme.backend.mapper.NotificationMapper;
import challengeme.backend.model.Notification;
import challengeme.backend.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import challengeme.backend.dto.request.update.NotificationUpdateRequest;
import challengeme.backend.dto.request.create.NotificationCreateRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

    public Notification getNotificationById(UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
    }

    public Notification createNotification(NotificationCreateRequest request) {
        Notification entity = mapper.toEntity(request);
        return notificationRepository.save(entity);
    }

    public Notification updateNotification(UUID id, NotificationUpdateRequest request) {
        Notification entity = getNotificationById(id); // aruncă excepție dacă nu există
        mapper.updateEntity(request, entity);
        return notificationRepository.save(entity);
    }

    public void deleteNotification(UUID id) {
        Notification entity = getNotificationById(id); // aruncă excepție dacă nu există
        notificationRepository.delete(entity);
    }

    @Transactional
    public void markAllNotificationsAsRead(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }
}