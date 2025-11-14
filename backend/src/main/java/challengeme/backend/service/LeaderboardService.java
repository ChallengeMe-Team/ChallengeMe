package challengeme.backend.service;

import challengeme.backend.exception.LeaderboardNotFoundException;
import challengeme.backend.model.Leaderboard;
import challengeme.backend.model.User;
import challengeme.backend.repository.LeaderboardRepository;
import challengeme.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepo;
    private final UserRepository userRepo;
@Autowired
    public LeaderboardService(LeaderboardRepository leaderboardRepo, UserRepository userRepo) {
        this.leaderboardRepo = leaderboardRepo;
        this.userRepo = userRepo;
    }

    public Leaderboard create(UUID userId, int totalPoints) {
        User user = userRepo.findById(userId);
        Leaderboard entry = new Leaderboard();
        entry.setUser(user);
        entry.setTotalPoints(totalPoints);
        leaderboardRepo.save(entry);
        recalcRanks();
        return entry;
    }

    public Leaderboard get(UUID id) {
        try {
            return leaderboardRepo.findById(id);
        } catch (Exception e) {
            throw new LeaderboardNotFoundException(id);
        }
    }

    public List<Leaderboard> getAll() {
        return leaderboardRepo.findAll();
    }

    public List<Leaderboard> getSorted() {
        return leaderboardRepo.findAll().stream()
                .sorted(Comparator.comparingInt(Leaderboard::getTotalPoints).reversed())
                .collect(Collectors.toList());
    }

    public Leaderboard update(UUID id, Integer totalPoints) {
        Leaderboard existing = get(id);
        if (totalPoints != null) {
            existing.setTotalPoints(totalPoints);
        }
        leaderboardRepo.update(existing);
        recalcRanks();
        return existing;
    }


    public void delete(UUID id) {
        try {
            leaderboardRepo.delete(id);
            recalcRanks();
        } catch (LeaderboardNotFoundException e) {
            throw new LeaderboardNotFoundException("Entry not found with id " + id);
        }
    }


    private void recalcRanks() {
        List<Leaderboard> sorted = getSorted();
        for (int i = 0; i < sorted.size(); i++) {
            Leaderboard e = sorted.get(i);
            e.setRank(i + 1);
            leaderboardRepo.update(e);
        }
    }
}
