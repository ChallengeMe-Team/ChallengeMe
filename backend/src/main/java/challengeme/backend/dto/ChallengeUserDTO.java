package challengeme.backend.dto;

import challengeme.backend.model.ChallengeUserStatus;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeUserDTO {
    private UUID id;
    private UUID userId;
    private String username;

    // --- Challenge Details ---
    private UUID challengeId;
    private String challengeTitle;      // Titlul
    private String description;         // Descrierea
    private Integer points;             // Punctele
    private String category;            // Categorie
    private String difficulty;          // Dificultate
    private String challengeCreatedBy;  // Cine a creat provocarea inițial
    private String assignedByUsername;

    // --- Status & Dates ---
    private ChallengeUserStatus status;
    private LocalDate dateAccepted;
    private LocalDate dateCompleted;
    private LocalDate deadline;         // Deadline-ul utilizatorului ("Until when")
}