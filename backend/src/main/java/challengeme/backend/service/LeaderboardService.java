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

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository repository;
    private final UserRepository userRepository;
    private final ChallengeUserRepository challengeUserRepository;
    private final LeaderboardMapper mapper;

    public Leaderboard create(UUID userId, int totalPoints) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Leaderboard entry = new Leaderboard(user, totalPoints);
        entry = repository.save(entry);

        recalcRanks();
        return entry;
    }

    public Leaderboard get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new LeaderboardNotFoundException(id));
    }

    public List<Leaderboard> getAll() {
        return repository.findAll();
    }

    public List<Leaderboard> getSorted() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Leaderboard::getTotalPoints).reversed())
                .toList();
    }

    public Leaderboard update(UUID id, LeaderboardUpdateRequest request) {
        Leaderboard existing = get(id);
        mapper.updateEntity(request, existing);
        repository.save(existing);
        recalcRanks();
        return existing;
    }

    public void delete(UUID id) {
        Leaderboard existing = get(id);
        repository.delete(existing);
        recalcRanks();
    }

    private void recalcRanks() {
        List<Leaderboard> sorted = getSorted();
        for (int i = 0; i < sorted.size(); i++) {
            Leaderboard e = sorted.get(i);
            e.setRank(i + 1);
            repository.save(e);
        }
    }

    public List<LeaderboardResponseDTO> getFilteredLeaderboard(LeaderboardRange range) {
        if (range == LeaderboardRange.ALL_TIME) {
            return getSorted().stream()
                    .map(e -> new LeaderboardResponseDTO(
                            e.getRank(),
                            e.getUser().getUsername(),
                            e.getUser().getAvatar(),
                            (long) e.getTotalPoints()))
                    .toList();
        }

        // Calculăm data de start
        LocalDate startDate = switch (range) {
            case WEEKLY -> LocalDate.now().minusDays(7);
            case MONTHLY -> LocalDate.now().minusMonths(1);
            default -> LocalDate.now().minusYears(100);
        };

        List<Object[]> results = challengeUserRepository.aggregateRankings(startDate);
        List<LeaderboardResponseDTO> ranking = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            ranking.add(new LeaderboardResponseDTO(
                    i + 1,                   // Rank calculat pe loc
                    (String) row[0],         // username
                    (String) row[1],         // avatar
                    (Long) row[2]            // total points sumate
            ));
        }
        return ranking;
    }

}
