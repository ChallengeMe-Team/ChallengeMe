package challengeme.backend.dto.request.update;

// !!!! Toate câmpurile sunt opționale
// !!!! Poate trimite doar ce vrea să modifice

public record UserUpdateRequest(
        String username,
        String email,
        String password,
        Integer points
) {}