package challengeme.backend.mapper;

import challengeme.backend.dto.request.create.UserCreateRequest;
import challengeme.backend.dto.UserDTO;
import challengeme.backend.dto.request.update.UserUpdateRequest;
import challengeme.backend.model.User;
import org.springframework.stereotype.Component;

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
                user.getRole()
        );
    }

    // Mapare Request -> Entitate (Folosit la creare manuală, deși AuthController face asta separat)
    public User toEntity(UserCreateRequest request) {
        if (request == null) return null;
        return new User(
                null,                   // ID (generat automat)
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                0,                      // Points default
                "user"                  // CRITIC: Trebuie setat un rol default aici pentru constructor
        );
    }

    // pentru update — nu creează un nou user, ci aplică pe unul existent
    // NU adaugam rolul
    public void updateEntity(UserUpdateRequest request, User user) {
        if (request.username() != null) user.setUsername(request.username());
        if (request.email() != null) user.setEmail(request.email());
        if (request.password() != null) user.setPassword(request.password());
        if (request.points() != null) user.setPoints(request.points());

        // NOTĂ: Nu permitem actualizarea rolului prin endpoint-ul standard de update profil!
        // Asta ar fi o problemă de securitate.
    }

}
