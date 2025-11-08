package challengeme.backend.service;

import challengeme.backend.model.Leaderboard;
import challengeme.backend.model.User;
import challengeme.backend.repository.LeaderboardRepository;

import challengeme.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepo;
    private final UserRepository userRepo;

    public LeaderboardService(LeaderboardRepository leaderboardRepo, UserRepository userRepo) {
        this.leaderboardRepo = leaderboardRepo;
        this.userRepo = userRepo;
    }

    public Leaderboard create(UUID userId, int totalPoints) {
        User user = userRepo.findById(userId);

        Leaderboard entry = new Leaderboard(null, user, totalPoints);
        leaderboardRepo.save(entry);
        recalcRanks();
        return entry;
    }


    public Leaderboard get(UUID id) {
        return leaderboardRepo.findById(id).orElseThrow(() -> new RuntimeException("Leaderboard not found: " + id));
    }

    public List<Leaderboard> getAll() { return leaderboardRepo.findAll(); }

    public List<Leaderboard> getSortedDescByPoints() {
        return leaderboardRepo.findAll().stream()
                .sorted(Comparator.comparingInt(Leaderboard::getTotalPoints).reversed()
                        .thenComparing(e -> e.getUser().getUsername(), Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public Leaderboard update(UUID id, Integer totalPoints) {
        Leaderboard existing = get(id);
        if (totalPoints != null) existing.setTotalPoints(totalPoints);
        leaderboardRepo.save(existing);
        recalcRanks();
        return existing;
    }

    public void delete(UUID id) {
        if (!leaderboardRepo.existsById(id)) throw new RuntimeException("Leaderboard not found: " + id);
        leaderboardRepo.deleteById(id);
        recalcRanks();
    }

    private void recalcRanks() {
        List<Leaderboard> sorted = getSortedDescByPoints();
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setRank(i + 1);
            leaderboardRepo.save(sorted.get(i));
        }
    }
}

