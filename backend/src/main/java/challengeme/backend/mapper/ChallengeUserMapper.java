package challengeme.backend.mapper;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.model.Challenge;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.repository.UserRepository; // Import necesar
import lombok.RequiredArgsConstructor; // Import necesar
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // 1. Adăugăm asta pentru injecție automată
public class ChallengeUserMapper {

    // 2. Injectăm repository-ul ca să putem căuta numele userului după ID
    private final UserRepository userRepository;

    public ChallengeUserDTO toDTO(ChallengeUser entity) {
        ChallengeUserDTO dto = new ChallengeUserDTO();

        // 1. ID Relație
        dto.setId(entity.getId());

        // 2. User Info (Cel care a primit provocarea)
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
        }

        // 3. Challenge Info
        if (entity.getChallenge() != null) {
            Challenge c = entity.getChallenge();
            dto.setChallengeId(c.getId());
            dto.setChallengeTitle(c.getTitle());

            // Câmpurile noi pentru UI
            dto.setDescription(c.getDescription());
            dto.setPoints(c.getPoints());
            dto.setCategory(c.getCategory());
            dto.setDifficulty(c.getDifficulty().toString());
            dto.setChallengeCreatedBy(c.getCreatedBy());
        }

        // 4. Status și Date
        dto.setStatus(entity.getStatus());
        dto.setDateAccepted(entity.getDateAccepted());
        dto.setDateCompleted(entity.getDateCompleted());
        dto.setDeadline(entity.getDeadline());

        dto.setTimesCompleted(entity.getTimesCompleted());

        // 5. Assigned By Username
        // Dacă există un ID în assignedBy, căutăm userul în bază și luăm numele
        if (entity.getAssignedBy() != null) {
            userRepository.findById(entity.getAssignedBy())
                    .ifPresent(u -> dto.setAssignedByUsername(u.getUsername()));
        }

        return dto;
    }
}