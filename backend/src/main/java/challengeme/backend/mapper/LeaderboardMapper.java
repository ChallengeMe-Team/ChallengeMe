package challengeme.backend.mapper;

import challengeme.backend.dto.LeaderboardDTO;
import challengeme.backend.dto.request.update.LeaderboardUpdateRequest;
import challengeme.backend.model.Leaderboard;
import org.springframework.stereotype.Component;

@Component
public class LeaderboardMapper {

    public LeaderboardDTO toDTO(Leaderboard entity) {
        return new LeaderboardDTO(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getUsername(),
                entity.getTotalPoints(),
                entity.getRank()
        );
    }

    public void updateEntity(LeaderboardUpdateRequest request, Leaderboard entity) {
        if (request.getTotalPoints() != null) {
            entity.setTotalPoints(request.getTotalPoints());
        }
    }
}
