package challengeme.backend.dto.request.create;

import jakarta.validation.constraints.NotBlank;

public record BadgeCreateRequest(
        @NotBlank String name,
        @NotBlank String description,
        String criteria,
        String iconUrl,
        Integer pointsReward
) {}