package challengeme.backend.model;

import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private Integer points;

    public User() {
        this.id = UUID.randomUUID();
    }

    public User(String username, String email, String password, Integer points) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        this.points = points == null ? 0 : points;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
