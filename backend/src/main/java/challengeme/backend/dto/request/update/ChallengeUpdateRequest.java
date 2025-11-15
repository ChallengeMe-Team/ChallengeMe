package challengeme.backend.dto.request.update;

import challengeme.backend.model.Challenge.Difficulty;

public record ChallengeUpdateRequest(
        String title,
        String description,
        String category,
        Difficulty difficulty,
        Integer points,
        String createdBy
) {}
