package challengeme.backend.dto;

import challengeme.backend.model.Challenge.Difficulty;

import java.util.UUID;

/**
 * Data Transfer Object for a Challenge definition.
 * Provides general information about a quest before a user accepts it.
 * @param id The unique identifier of the challenge.
 * @param title Public name of the quest.
 * @param description Detailed instructions or lore for the challenge.
 * @param category Thematic grouping (e.g., Coding, Fitness).
 * @param difficulty Complexity level (EASY, MEDIUM, HARD).
 * @param points Points granted upon successful completion.
 * @param createdBy The username of the user who designed this challenge.
 */
public record ChallengeDTO(
        UUID id,
        String title,
        String description,
        String category,
        Difficulty difficulty,
        int points,
        String createdBy
) {}
