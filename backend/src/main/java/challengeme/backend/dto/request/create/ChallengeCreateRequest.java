package challengeme.backend.dto.request.create;

import challengeme.backend.model.Challenge.Difficulty;
import challengeme.backend.validation.ValidContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data transfer object used for initiating a new Challenge in the system.
 * Includes validation for content integrity and point distribution.
 * * @param title The public name of the challenge.
 * @param description Details about the tasks required for the challenge.
 * @param category The thematic group (e.g., Fitness, Coding).
 * @param difficulty Complexity level (EASY, MEDIUM, HARD).
 * @param points Reward value for successful completion.
 * @param createdBy The username of the user who designed the challenge.
 */
public record ChallengeCreateRequest(
        @NotBlank @ValidContent String title,
        @ValidContent String description,
        @NotBlank String category,
        @NotNull Difficulty difficulty,
        @Positive int points,
        @NotBlank String createdBy
) {}
