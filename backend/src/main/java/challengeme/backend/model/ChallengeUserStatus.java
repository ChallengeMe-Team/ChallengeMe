package challengeme.backend.model;

public enum ChallengeUserStatus {
    PENDING,  // Provocarea a fost văzută, dar nu acceptată încă (sau e default)
    ACCEPTED, // Utilizatorul a acceptat provocarea
    COMPLETED
}
