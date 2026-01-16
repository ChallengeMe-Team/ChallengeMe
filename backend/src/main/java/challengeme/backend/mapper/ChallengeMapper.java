package challengeme.backend.mapper;

import challengeme.backend.dto.request.create.ChallengeCreateRequest;
import challengeme.backend.dto.ChallengeDTO;
import challengeme.backend.dto.request.update.ChallengeUpdateRequest;
import challengeme.backend.model.Challenge;
import org.springframework.stereotype.Component;

/**
 * Mapper component dedicated to Challenge entities.
 * Facilitates the conversion between the database model and the data transfer objects
 * required for creating, retrieving, and updating challenges.
 */
@Component
public class ChallengeMapper {

    /**
     * Converts a Challenge entity into a ChallengeDTO.
     * Used for sending challenge details to the frontend while keeping the internal
     * entity structure encapsulated.
     * * @param entity The Challenge entity to be converted.
     * @return A DTO containing the public challenge information.
     */
    public ChallengeDTO toDTO(Challenge entity) {
        return new ChallengeDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getDifficulty(),
                entity.getPoints(),
                entity.getCreatedBy()
        );
    }

    /**
     * Transforms a ChallengeCreateRequest into a new Challenge entity.
     * Maps user input from the creation form to a persistable entity.
     * * @param request The DTO containing the parameters for the new challenge.
     * @return A Challenge entity ready to be saved to the database.
     */
    public Challenge toEntity(ChallengeCreateRequest request) {
        Challenge entity = new Challenge();
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setCategory(request.category());
        entity.setDifficulty(request.difficulty());
        entity.setPoints(request.points());
        entity.setCreatedBy(request.createdBy());
        return entity;
    }

    /**
     * Updates an existing Challenge entity with data from a ChallengeUpdateRequest.
     * Supports partial updates by checking for non-null fields in the request object.
     * * @param request The DTO containing the fields to be updated.
     * @param entity The target Challenge entity to be modified.
     */
    public void updateEntity(ChallengeUpdateRequest request, Challenge entity) {
        if (request.title() != null) entity.setTitle(request.title());
        if (request.description() != null) entity.setDescription(request.description());
        if (request.category() != null) entity.setCategory(request.category());
        if (request.difficulty() != null) entity.setDifficulty(request.difficulty());
        if (request.points() != null) entity.setPoints(request.points());
        if (request.createdBy() != null) entity.setCreatedBy(request.createdBy());
    }
}
