package challengeme.backend.mapper;

import challengeme.backend.dto.request.create.UserCreateRequest;
import challengeme.backend.dto.UserDTO;
import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPoints()
        );
    }

    public User toEntity(UserCreateRequest request) {
        return new User(
                null,
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                0
        );
    }

    // pentru update — nu creează un nou user, ci aplică pe unul existent
    public void updateEntity(UserUpdateRequest request, User user) {
        if (request.username() != null) user.setUsername(request.username());
        if (request.email() != null) user.setEmail(request.email());
        if (request.password() != null) user.setPassword(request.password());
        if (request.points() != null) user.setPoints(request.points());
    }

}
