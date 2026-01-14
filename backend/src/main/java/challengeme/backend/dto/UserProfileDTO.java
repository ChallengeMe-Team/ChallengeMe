package challengeme.backend.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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