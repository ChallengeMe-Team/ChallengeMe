package challengeme.backend.dto.request.update;

/**
 * Request DTO for updating an existing Badge definition.
 * All fields are optional, allowing for partial updates of the badge metadata.
 * @param name The updated name of the badge.
 * @param description The updated description.
 * @param criteria The updated rules for earning this achievement.
 */
public record BadgeUpdateRequest(
        String name,
        String description,
        String criteria
) {}
