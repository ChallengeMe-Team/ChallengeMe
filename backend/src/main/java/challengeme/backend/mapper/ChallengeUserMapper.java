package challengeme.backend.mapper;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.model.Challenge;
import challengeme.backend.model.ChallengeUser;
import org.springframework.stereotype.Component;

@Component
public class ChallengeUserMapper {

    public ChallengeUserDTO toDTO(ChallengeUser entity) {
        ChallengeUserDTO dto = new ChallengeUserDTO();

        // 1. ID Relație
        dto.setId(entity.getId());

        // 2. User Info
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
        }

        // 3. Challenge Info
        if (entity.getChallenge() != null) {
            Challenge c = entity.getChallenge();
            dto.setChallengeId(c.getId());
            dto.setChallengeTitle(c.getTitle());

            // Câmpurile noi pentru UI
            dto.setDescription(c.getDescription());
            dto.setPoints(c.getPoints());
            dto.setCategory(c.getCategory());
            dto.setDifficulty(c.getDifficulty().toString());
            dto.setChallengeCreatedBy(c.getCreatedBy());
        }

        // 4. Status și Date
        dto.setStatus(entity.getStatus());
        dto.setDateAccepted(entity.getDateAccepted());
        dto.setDateCompleted(entity.getDateCompleted());
        dto.setDeadline(entity.getDeadline());

        return dto;
    }
}