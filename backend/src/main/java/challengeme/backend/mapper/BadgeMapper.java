package challengeme.backend.mapper;

import challengeme.backend.dto.request.update.BadgeUpdateRequest;
import challengeme.backend.dto.BadgeDTO;
import challengeme.backend.dto.request.create.BadgeCreateRequest;
import challengeme.backend.model.Badge;
import org.springframework.stereotype.Component;

@Component
public class BadgeMapper {

    public BadgeDTO toDTO(Badge entity) {
        if (entity == null) {
            return null;
        }
        return new BadgeDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCriteria(),
                entity.getIconUrl(),
                entity.getPointsReward()
        );
    }

    public Badge toEntity(BadgeCreateRequest request) {
        Badge entity = new Badge();
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setCriteria(request.criteria());
        entity.setIconUrl(request.iconUrl());
        entity.setPointsReward(request.pointsReward());
        return entity;
    }

    public void updateEntity(BadgeUpdateRequest request, Badge entity) {
        if (request.name() != null) entity.setName(request.name());
        if (request.description() != null) entity.setDescription(request.description());
        if (request.criteria() != null) entity.setCriteria(request.criteria());
    }
}
