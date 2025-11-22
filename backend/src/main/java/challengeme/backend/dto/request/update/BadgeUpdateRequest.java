package challengeme.backend.dto.request.update;

public record BadgeUpdateRequest(
        String name,
        String description,
        String criteria
) {}
