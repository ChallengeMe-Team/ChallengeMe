package challengeme.backend.domain;

import java.util.UUID;

public class Challenge {
    private UUID id;
    private String title;
    private String description;
    private String category;
    private Difficulty difficulty;
    private int points;
    private String createdBy;

    public enum Difficulty{
        EASY, MEDIUM, HARD
    }

    public Challenge() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public Difficulty getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Challenge(String title, String description,String category, Difficulty difficulty, int points, String createdBy) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.points = points;
        this.createdBy = createdBy;
        this.category = category;
    }
}
