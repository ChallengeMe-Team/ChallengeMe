package challengeme.backend.mapper;

import challengeme.backend.dto.UserBadgeDTO;
import challengeme.backend.dto.request.update.UserBadgeUpdateRequest;
import challengeme.backend.model.UserBadge;
import org.springframework.stereotype.Component;

/**
 * Mapper component responsible for transforming UserBadge entities into DTOs.
 * It flattens the relationship between a User and an earned Badge, providing
 * a comprehensive view of the achievement for the frontend.
 */
@Component
public class UserBadgeMapper {

    /**
     * Converts a UserBadge entity into a UserBadgeDTO.
     * Aggregates data from the associated User and Badge entities to create
     * a detailed achievement record including badge metadata and award date.
     * * @param entity The UserBadge entity representing a specific earned achievement.
     * @return A UserBadgeDTO containing flattened user and badge details.
     */
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

    /**
     * Updates an existing UserBadge entity with data from a UserBadgeUpdateRequest.
     * Allows for administrative adjustment of the achievement's timestamp.
     * * @param request The DTO containing the updated award date.
     * @param entity The target UserBadge entity to be modified.
     */
    public void updateEntity(UserBadgeUpdateRequest request, UserBadge entity) {
        if (request.getDateAwarded() != null) {
            entity.setDateAwarded(request.getDateAwarded());
        }
    }
}