package challengeme.backend.dto.request.create;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for creating a new Badge definition.
 * Encapsulates all necessary metadata to define a reward in the system.
 * * @param name The unique name of the badge.
 * @param description A brief explanation of what the badge represents.
 * @param criteria The specific rules or requirements to unlock this badge.
 * @param iconUrl The path or URL to the visual representation of the badge.
 * @param pointsReward Optional bonus points awarded upon receiving this badge.
 */
public record BadgeCreateRequest(
        @NotBlank String name,
        @NotBlank String description,
        String criteria,
        String iconUrl,
        Integer pointsReward
) {}