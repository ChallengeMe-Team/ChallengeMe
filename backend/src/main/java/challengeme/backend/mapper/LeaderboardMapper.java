package challengeme.backend.mapper;

import challengeme.backend.dto.LeaderboardDTO;
import challengeme.backend.dto.request.update.LeaderboardUpdateRequest;
import challengeme.backend.model.Leaderboard;
import org.springframework.stereotype.Component;

/**
 * Mapper component responsible for converting Leaderboard entities to DTOs.
 * It facilitates the display of user rankings by mapping internal point records
 * to a structured format used by the leaderboard UI.
 */
@Component
public class LeaderboardMapper {

    /**
     * Converts a Leaderboard entity into a LeaderboardDTO.
     * Extracts user-specific details (ID and Username) from the associated User entity
     * to provide context for the ranking.
     * * @param entity The Leaderboard entity containing score and rank data.
     * @return A DTO suitable for displaying a user's position in the rankings.
     */
    public LeaderboardDTO toDTO(Leaderboard entity) {
        return new LeaderboardDTO(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getUsername(),
                entity.getTotalPoints(),
                entity.getRank()
        );
    }

    /**
     * Updates an existing Leaderboard entity with new point data.
     * Primarily used for administrative adjustments or automated point synchronization.
     * * @param request The DTO containing the updated total points.
     * @param entity The target Leaderboard entity to be modified.
     */
    public void updateEntity(LeaderboardUpdateRequest request, Leaderboard entity) {
        if (request.getTotalPoints() != null) {
            entity.setTotalPoints(request.getTotalPoints());
        }
    }
}
