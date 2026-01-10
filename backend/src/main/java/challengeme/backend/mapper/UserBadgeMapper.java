package challengeme.backend.mapper;

import challengeme.backend.dto.UserBadgeDTO;
import challengeme.backend.dto.request.update.UserBadgeUpdateRequest;
import challengeme.backend.model.UserBadge;
import org.springframework.stereotype.Component;

@Component
public class UserBadgeMapper {

    public UserBadgeDTO toDTO(UserBadge entity) {
        return new UserBadgeDTO(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getUsername(),
                entity.getBadge().getId(),
                entity.getBadge().getName(),
                entity.getBadge().getDescription(),
                entity.getBadge().getIconUrl(),
                entity.getBadge().getPointsReward(),
                entity.getDateAwarded()
        );
    }

    public void updateEntity(UserBadgeUpdateRequest request, UserBadge entity) {
        if (request.getDateAwarded() != null) {
            entity.setDateAwarded(request.getDateAwarded());
        }
    }
}