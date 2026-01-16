package challengeme.backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Intercepts all exceptions thrown across the application and converts them
 * into standardized JSON responses for the frontend.
 * Provides a clean API interface by hiding stack traces and showing user-friendly messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catches validation errors triggered by @Valid annotations in controllers.
     * @param ex The exception containing binding and validation results.
     * @return 400 Bad Request with the first validation error message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(err -> err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        Map<String, String> response = new HashMap<>();
        response.put("error", errorMessage);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all resources not found errors (404).
     * @param ex The specific NotFoundException instance.
     * @return Standardized JSON error message.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 3. Argument ilegal
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> resp = new HashMap<>();
        resp.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    /**
     * Processes database integrity issues, specifically Unique Constraint violations.
     * Interprets PostgreSQL error codes to provide friendly messages for duplicate usernames/emails.
     * @param ex Exception thrown during failed persistence.
     * @return 409 Conflict with a specific message about the duplicate field.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        String msg = ex.getMessage();

        if (msg != null && msg.contains("users_username_key")) {
            error.put("message", "Acest nume de utilizator este deja folosit.");
        } else if (msg != null && msg.contains("users_email_key")) {
            error.put("message", "Această adresă de email este deja folosită.");
        } else {
            error.put("message", "Datele introduse există deja în sistem.");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflictException(ConflictException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Final fallback for any unhandled server-side exceptions (500).
     * Logs the stack trace internally and returns a generic safe message to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        Map<String, String> body = new HashMap<>();
        body.put("message", "A apărut o eroare neașteptată pe server.");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
