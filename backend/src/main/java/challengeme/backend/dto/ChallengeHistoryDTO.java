package challengeme.backend.dto;

public record ChallengeHistoryDTO(
    String challengeTitle,
    String status, // "COMPLETED"
    String date,    // "2 days ago" sau data formatată
    Integer timesCompleted
) {}