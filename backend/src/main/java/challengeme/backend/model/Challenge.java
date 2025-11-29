package challengeme.backend.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "challenges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Difficulty must be specified")
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Positive(message = "Points must be positive")
    private int points;

    @NotBlank(message = "Created by is required")
    private String createdBy;

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}