package challengeme.backend.mapper;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.model.ChallengeUser;
import org.springframework.stereotype.Component;

@Component
public class ChallengeUserMapper {

    public ChallengeUserDTO toDTO(ChallengeUser entity) {
        return new ChallengeUserDTO(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getUsername(),
                entity.getChallenge().getId(),
                entity.getChallenge().getTitle(),
                entity.getStatus(),
                entity.getDateAccepted(),
                entity.getDateCompleted()
        );
    }
}
