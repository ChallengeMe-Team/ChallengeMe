package challengeme.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import jakarta.persistence.*;

/**
 * Entity representing the 'challenges' table.
 * Contains the definition of a quest, including its difficulty and point value.
 */
@Entity
@Table(name = "challenges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {

    @Id
    @GeneratedValue
    private UUID id;

    /** The title of the challenge as seen in the public catalog. */
    @NotBlank(message = "Title is required")
    private String title;

    /** Detailed information about what the user needs to perform. */
    private String description;

    /** The category (e.g., 'Mental', 'Physical') for skill breakdown tracking. */
    @NotBlank(message = "Category is required")
    private String category;

    /** Enumerated complexity of the quest (EASY, MEDIUM, HARD). */
    @NotNull(message = "Difficulty must be specified")
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    /** The number of points awarded upon completion. Must be a positive value. */
    @Positive(message = "Points must be positive")
    private int points;

    /** Records the username of the user who created this challenge. */
    @NotBlank(message = "Created by is required")
    private String createdBy;

    /**
     * Enum defining the possible difficulty levels for a challenge.
     */
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}