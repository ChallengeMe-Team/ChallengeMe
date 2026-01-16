package challengeme.backend.dto.request.update;

import challengeme.backend.model.Challenge.Difficulty;

/**
 * Data transfer object used to modify an existing global challenge.
 * Enables creators or admins to adjust difficulty, points, or descriptive content.
 * @param title New title for the quest.
 * @param description Updated task details.
 * @param category The updated thematic category.
 * @param difficulty Updated difficulty level (EASY, MEDIUM, HARD).
 * @param points Updated reward value.
 * @param createdBy The creator's identifier (used for ownership validation).
 */
public record ChallengeUpdateRequest(
        String title,
        String description,
        String category,
        Difficulty difficulty,
        Integer points,
        String createdBy
) {}
