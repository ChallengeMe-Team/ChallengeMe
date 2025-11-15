package challengeme.backend.dto;

import challengeme.backend.model.Challenge.Difficulty;

import java.util.UUID;

public record ChallengeDTO(
        UUID id,
        String title,
        String description,
        String category,
        Difficulty difficulty,
        int points,
        String createdBy
) {}
