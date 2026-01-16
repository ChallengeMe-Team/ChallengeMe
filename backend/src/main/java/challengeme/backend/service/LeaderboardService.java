package challengeme.backend.service;

import challengeme.backend.dto.LeaderboardResponseDTO;
import challengeme.backend.dto.request.update.LeaderboardUpdateRequest;
import challengeme.backend.exception.LeaderboardNotFoundException;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.model.Leaderboard;
import challengeme.backend.model.LeaderboardRange;
import challengeme.backend.model.User;
import challengeme.backend.repository.ChallengeUserRepository;
import challengeme.backend.repository.LeaderboardRepository;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import challengeme.backend.mapper.LeaderboardMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static challengeme.backend.model.LeaderboardRange.MONTHLY;
import static challengeme.backend.model.LeaderboardRange.WEEKLY;
import static challengeme.backend.model.LeaderboardRange.LAST_6_MONTHS;

/**
 * Service responsible for managing user rankings and competitive statistics.
 * It provides both persistent ranking records and dynamic leaderboard calculations
 * based on specific time periods (Weekly, Monthly, All Time).
 */
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository repository;
    private final UserRepository userRepository;
    private final ChallengeUserRepository challengeUserRepository;
    private final LeaderboardMapper mapper;

    /**
     * Manually creates a leaderboard entry for a user.
     * Automatically triggers a rank recalculation for the entire system.
     * @param userId UUID of the user.
     * @param totalPoints Starting points for the entry.
     * @return The saved Leaderboard entity.
     */
    public Leaderboard create(UUID userId, int totalPoints) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Leaderboard entry = new Leaderboard(user, totalPoints);
        entry = repository.save(entry);

        recalcRanks();
        return entry;
    }

    /** Retrieves a specific leaderboard record by ID. */
    public Leaderboard get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new LeaderboardNotFoundException(id));
    }

    public List<Leaderboard> getAll() {
        return repository.findAll();
    }

    /** Returns all leaderboard entries sorted by total points in descending order. */
    public List<Leaderboard> getSorted() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Leaderboard::getTotalPoints).reversed())
                .toList();
    }

    /** Updates points for an entry and refreshes global rankings. */
    public Leaderboard update(UUID id, LeaderboardUpdateRequest request) {
        Leaderboard existing = get(id);
        mapper.updateEntity(request, existing);
        repository.save(existing);
        recalcRanks();
        return existing;
    }

    /** Deletes an entry and updates the ranks of remaining participants. */
    public void delete(UUID id) {
        Leaderboard existing = get(id);
        repository.delete(existing);
        recalcRanks();
    }

    /**
     * Internal logic to update numerical ranks (1st, 2nd, 3rd, etc.)
     * based on the current sorted point values in the database.
     */
    private void recalcRanks() {
        List<Leaderboard> sorted = getSorted();
        for (int i = 0; i < sorted.size(); i++) {
            Leaderboard e = sorted.get(i);
            e.setRank(i + 1);
            repository.save(e);
        }
    }

    /**
     * Main engine for the Leaderboard UI. Provides two types of rankings:
     * 1. ALL_TIME: Fetches direct points from the User profile.
     * 2. PERIODIC (Weekly/Monthly/6 Months): Aggregates points earned
     * from challenges completed within the specified timeframe.
     * @param range The desired time scope for the ranking.
     * @return A list of DTOs ready for display, sorted by rank.
     */
    public List<LeaderboardResponseDTO> getFilteredLeaderboard(LeaderboardRange range) {

        List<Object[]> results;

        if (range == LeaderboardRange.ALL_TIME) {
            // Case 1: Global ranking based on total accumulated XP in user profiles
            results = userRepository.findGlobalLeaderboard();
        } else {
            // Case 2: Dynamic ranking based on completion dates in challenge_users
            LocalDate startDate = switch (range) {
                case WEEKLY -> LocalDate.now().minusDays(7);
                case MONTHLY -> LocalDate.now().minusMonths(1);
                case LAST_6_MONTHS -> LocalDate.now().minusMonths(6);
                default -> LocalDate.now(); // Fallback
            };
            results = challengeUserRepository.aggregateRankings(startDate);
        }

        List<LeaderboardResponseDTO> ranking = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);

            // Handle numeric casting safely (PostgreSQL SUM returns Long, User points might be Integer)
            Long points = (row[2] instanceof Number) ? ((Number) row[2]).longValue() : 0L;

            ranking.add(new LeaderboardResponseDTO(
                    i + 1,                   // Rank
                    (String) row[0],         // Username
                    (String) row[1],         // Avatar
                    points                // total points
            ));
        }
        return ranking;
    }

}
