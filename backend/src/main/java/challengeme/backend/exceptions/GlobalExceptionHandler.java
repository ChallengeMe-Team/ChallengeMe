package challengeme.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChallengeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleChallengeNotFound(ChallengeNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
//        Map<String, Object> error = new HashMap<>();
//        error.put("timestamp", LocalDateTime.now());
//        // Collect all validation messages into one string
//        String message = ex.getBindingResult().getFieldErrors().stream()
//                .map(f -> f.getField() + ": " + f.getDefaultMessage())
//                .reduce((a, b) -> a + "; " + b)
//                .orElse("Invalid request");
//        error.put("message", message);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//    }
}
