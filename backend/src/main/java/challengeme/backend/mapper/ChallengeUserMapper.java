package challengeme.backend.mapper;

import challengeme.backend.dto.ChallengeUserDTO;
import challengeme.backend.model.Challenge;
import challengeme.backend.model.ChallengeUser;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Advanced mapper component for the ChallengeUser relationship.
 * It performs data flattening by merging challenge metadata and assigner details
 * into a single DTO to optimize frontend rendering.
 */
@Component
@RequiredArgsConstructor
public class ChallengeUserMapper {

    /** Repository used to resolve usernames for social assignments (assignedBy). */
    private final UserRepository userRepository;

    /**
     * Transforms a ChallengeUser entity into a rich ChallengeUserDTO.
     * This method populates the DTO with aggregated data from associated Challenge and User entities.
     * * @param entity The participation record linking a user to a quest.
     * @return A comprehensive DTO containing quest details, status, and social context.
     */
    public ChallengeUserDTO toDTO(ChallengeUser entity) {
        ChallengeUserDTO dto = new ChallengeUserDTO();

        // 1. Primary Identifier
        dto.setId(entity.getId());

        // 2. Participant Information
        // Maps the ID and Username of the user currently performing the challenge.
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
        }

        // 3. Flattened Challenge Metadata
        // Extracts and flattens challenge details to prevent the frontend from
        // needing separate calls to the /challenges endpoint.
        if (entity.getChallenge() != null) {
            Challenge c = entity.getChallenge();
            dto.setChallengeId(c.getId());
            dto.setChallengeTitle(c.getTitle());

            dto.setDescription(c.getDescription());
            dto.setPoints(c.getPoints());
            dto.setCategory(c.getCategory());
            dto.setDifficulty(c.getDifficulty().toString());
            dto.setChallengeCreatedBy(c.getCreatedBy());
        }

        // 4. Progress Tracking and Temporal Data
        // Includes current status, timestamps for acceptance/completion, and user-defined deadlines.
        dto.setStatus(entity.getStatus());
        dto.setStartDate(entity.getStartDate());
        dto.setDateAccepted(entity.getDateAccepted());
        dto.setDateCompleted(entity.getDateCompleted());
        dto.setDeadline(entity.getDeadline());
        dto.setTimesCompleted(entity.getTimesCompleted());

        // 5. Social Context (Assignment Resolution)
        // If the challenge was assigned by another user, the assigner's ID is resolved
        // into a displayable username for the UI's "Assigned by" label.
        if (entity.getAssignedBy() != null) {
            userRepository.findById(entity.getAssignedBy())
                    .ifPresent(u -> dto.setAssignedByUsername(u.getUsername()));
        }

        return dto;
    }
}