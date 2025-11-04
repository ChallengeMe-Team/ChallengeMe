package challengeme.backend.model;

import lombok.Data;

/**
 * DTO folosit pentru a actualiza statusul unei provocări.
 */
@Data
public class UpdateChallengeStatusRequest {
    private ChallengeUserStatus status;

}
