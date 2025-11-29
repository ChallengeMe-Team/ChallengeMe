package challengeme.backend.service;

import challengeme.backend.dto.request.update.LeaderboardUpdateRequest;
import challengeme.backend.exception.LeaderboardNotFoundException;
import challengeme.backend.exception.UserNotFoundException;
import challengeme.backend.model.Leaderboard;
import challengeme.backend.model.User;
import challengeme.backend.repository.LeaderboardRepository;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import challengeme.backend.mapper.LeaderboardMapper;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository repository;
    private final UserRepository userRepository;
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

}
