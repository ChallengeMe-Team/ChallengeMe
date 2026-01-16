package challengeme.backend.dto;

/**
 * Specialized DTO for the User Profile's "Recent Activity" section.
 * Designed to provide a snapshot of past participations.
 * @param challengeTitle The name of the challenge associated with this record.
 * @param status The completion state (e.g., "COMPLETED").
 * @param date Formatted time string (e.g., "Just now", "2 days ago").
 * @param timesCompleted Number of times the user has successfully finished this quest.
 */
public record ChallengeHistoryDTO(
    String challengeTitle,
    String status,
    String date,
    Integer timesCompleted
) {}