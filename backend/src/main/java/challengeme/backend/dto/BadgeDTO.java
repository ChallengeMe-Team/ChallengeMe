package challengeme.backend.dto;

import java.util.UUID;

public record BadgeDTO(
        UUID id,
        String name,
        String description,
        String criteria
) {}
