package challengeme.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionează centralizat toate excepțiile pentru controllerele REST,
 * asigurând un format de răspuns consistent pentru erori.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestionează erorile de validare a datelor de intrare (ex: DTO-uri).
     * Returnează un obiect detaliat cu erorile pentru fiecare câmp.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                fieldErrors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gestionează excepțiile când o resursă nu este găsită (ex: Challenge, UserBadge).
     * Folosim o clasă de bază comună (NotFoundException) pentru a evita duplicarea.
     * Vezi sugestia de mai jos pentru a crea NotFoundException.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundExceptions(NotFoundException ex) {
        Map<String, Object> errorResponse = createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Gestionează argumentele ilegale pasate metodelor din service layer.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
            * Metodă helper pentru a crea un corp de răspuns standardizat pentru erori.
            *
            * @param status HttpStatus-ul erorii.
            * @param message Mesajul principal al erorii.
     * @param details Detalii suplimentare (ex: erori de validare pe câmpuri).
            * @return Un Map ce reprezintă corpul răspunsului JSON.
     */
    private Map<String, Object> createErrorResponse(HttpStatus status, String message, Object details) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        if (details != null) {
            errorResponse.put("details", details);
        }
        return errorResponse;
    }
}
