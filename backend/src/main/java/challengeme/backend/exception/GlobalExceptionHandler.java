package challengeme.backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Validation errors (unic!)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid value"
                ));

        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("errors", fieldErrors);
        body.put("message", "Validation failed");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 2. Not found – UNIC și GLOBAL
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

    // === 4. HANDLER NOU: Duplicate în baza de date (Username/Email există deja) ===
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        String msg = ex.getMessage(); // Mesajul detaliat de la PostgreSQL

        // Verificăm ce anume a cauzat eroarea (username sau email)
        if (msg != null && msg.contains("users_username_key")) {
            error.put("message", "Acest nume de utilizator este deja folosit.");
        } else if (msg != null && msg.contains("users_email_key")) {
            error.put("message", "Această adresă de email este deja folosită.");
        } else {
            error.put("message", "Datele introduse există deja în sistem.");
        }

        // Returnăm 409 Conflict (nu 500)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 5. Orice altă eroare neașteptată (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        ex.printStackTrace(); // Printează eroarea în consolă în IntelliJ pentru tine
        Map<String, String> body = new HashMap<>();
        body.put("message", "A apărut o eroare neașteptată pe server.");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
