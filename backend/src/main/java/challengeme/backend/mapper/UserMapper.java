package challengeme.backend.mapper;

import challengeme.backend.dto.FriendDTO;
import challengeme.backend.dto.request.create.UserCreateRequest;
import challengeme.backend.dto.UserDTO;
import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapper {

    // Mapare Entitate -> DTO (Trimitem rolul către frontend)
    public UserDTO toDTO(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPoints(),
                user.getAvatar(),
                user.getRole()
        );
    }

    public FriendDTO toFriendDTO(User user) {
        if (user == null) return null;
        return new FriendDTO(
                user.getId(),
                user.getUsername(),
                user.getPoints(),
                user.getAvatar()
        );
    }

    // Mapare Request -> Entitate (Folosit la creare manuală, deși AuthController face asta separat)
    public User toEntity(UserCreateRequest request) {
        if (request == null) return null;
        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPoints(0);
        // Default avatar la creare
        user.setAvatar("gamer.png");
        user.setRole("user");
        return user;
    }

    // pentru update — nu creează un nou user, ci aplică pe unul existent
    // NU adaugam rolul
    public void updateEntity(UserUpdateRequest request, User user) {
        if (request.username() != null) user.setUsername(request.username());
        if (request.email() != null) user.setEmail(request.email());
        if (request.password() != null) user.setPassword(request.password());
        if (request.points() != null) user.setPoints(request.points());
        if (request.avatar() != null) user.setAvatar(request.avatar());
    }

}
