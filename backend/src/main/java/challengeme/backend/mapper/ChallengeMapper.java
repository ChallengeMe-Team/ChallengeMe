package challengeme.backend.mapper;

import challengeme.backend.dto.request.create.ChallengeCreateRequest;
import challengeme.backend.dto.ChallengeDTO;
import challengeme.backend.dto.request.update.ChallengeUpdateRequest;
import challengeme.backend.model.Challenge;
import org.springframework.stereotype.Component;

@Component
public class ChallengeMapper {

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

    public void updateEntity(ChallengeUpdateRequest request, Challenge entity) {
        if (request.title() != null) entity.setTitle(request.title());
        if (request.description() != null) entity.setDescription(request.description());
        if (request.category() != null) entity.setCategory(request.category());
        if (request.difficulty() != null) entity.setDifficulty(request.difficulty());
        if (request.points() != null) entity.setPoints(request.points());
        if (request.createdBy() != null) entity.setCreatedBy(request.createdBy());
    }
}
