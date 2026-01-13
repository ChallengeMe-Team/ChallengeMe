package challengeme.backend.dto.request.create;

import challengeme.backend.model.Challenge.Difficulty;
import challengeme.backend.validation.ValidContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChallengeCreateRequest(
        @NotBlank @ValidContent String title,
        @ValidContent String description,
        @NotBlank String category,
        @NotNull Difficulty difficulty,
        @Positive int points,
        @NotBlank String createdBy
) {}
