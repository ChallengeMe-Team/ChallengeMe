package challengeme.backend.dto;
public record LeaderboardResponseDTO(
    int rank,
    String username,
    String avatar,
    long points
) {}