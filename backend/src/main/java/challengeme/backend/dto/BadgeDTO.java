package challengeme.backend.dto;

import java.util.UUID;

/**
 * Data Transfer Object representing a Badge in the global catalog.
 * Used to display available rewards to the user.
 * @param id The unique identifier of the badge.
 * @param name Display name of the achievement.
 * @param description Detailed explanation of the badge's meaning.
 * @param criteria Requirements defined to unlock this badge.
 * @param iconUrl Path to the visual asset for the badge icon.
 * @param pointsReward Bonus points awarded when the badge is earned.
 */
public record BadgeDTO(
        UUID id,
        String name,
        String description,
        String criteria,
        String iconUrl,
        Integer pointsReward
) {}
