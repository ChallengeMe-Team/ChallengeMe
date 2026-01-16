package challengeme.backend.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Aggregated DTO representing the complete User Profile view.
 * Compiles stats, achievements, and activity history into a single object for the Frontend.
 */
public record UserProfileDTO(
        UUID id,
        String username,
        String email,
        Integer points,
        Integer level,
        String avatar,
        int totalCompletedChallenges,
        int currentStreak,
        List<BadgeDTO> badges,
        List<ChallengeHistoryDTO> recentActivity,
        Map<String, Integer> skillBreakdown
) {}