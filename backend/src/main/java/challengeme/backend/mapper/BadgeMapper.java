package challengeme.backend.mapper;

import challengeme.backend.dto.request.update.BadgeUpdateRequest;
import challengeme.backend.dto.BadgeDTO;
import challengeme.backend.dto.request.create.BadgeCreateRequest;
import challengeme.backend.model.Badge;
import org.springframework.stereotype.Component;

/**
 * Mapper component responsible for converting between Badge entities and their respective DTOs.
 * Centralizes the transformation logic to ensure consistency across the application layers.
 */
@Component
public class BadgeMapper {

    /**
     * Converts a Badge entity into a BadgeDTO for API responses.
     * * @param entity The Badge entity from the database.
     * @return A BadgeDTO containing the badge metadata, or null if the entity is null.
     */
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

    /**
     * Maps a BadgeCreateRequest DTO to a new Badge entity.
     * Used during the badge creation process to initialize a new record.
     * * @param request The DTO containing the creation data.
     * @return A new Badge entity populated with data from the request.
     */
    public Badge toEntity(BadgeCreateRequest request) {
        Badge entity = new Badge();
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setCriteria(request.criteria());
        entity.setIconUrl(request.iconUrl());
        entity.setPointsReward(request.pointsReward());
        return entity;
    }

    /**
     * Updates an existing Badge entity using data from a BadgeUpdateRequest.
     * Only non-null fields from the request are applied to the entity (Partial Update).
     * * @param request The DTO containing the updated fields.
     * @param entity The existing Badge entity to be modified.
     */
    public void updateEntity(BadgeUpdateRequest request, Badge entity) {
        if (request.name() != null) entity.setName(request.name());
        if (request.description() != null) entity.setDescription(request.description());
        if (request.criteria() != null) entity.setCriteria(request.criteria());
    }
}
