package challengeme.backend.dto.request.create;

import challengeme.backend.model.Challenge.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChallengeCreateRequest(
        @NotBlank String title,
        String description,
        @NotBlank String category,
        @NotNull Difficulty difficulty,
        @Positive int points,
        @NotBlank String createdBy
) {}
