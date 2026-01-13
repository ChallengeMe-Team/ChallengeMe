package challengeme.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.core.io.ClassPathResource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class ContentValidator implements ConstraintValidator<ValidContent, String> {
    private static final Set<String> FORBIDDEN_WORDS = new HashSet<>();

    static {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("bad-words.txt").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Curățăm linia de spații și caractere invizibile
                String word = line.trim().toLowerCase().replaceAll("[\\p{Cf}]", "");
                if (!word.isEmpty()) {
                    FORBIDDEN_WORDS.add(word);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading bad-words.txt");
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        String input = value.toLowerCase();

        // Verificăm dacă inputul conține oricare dintre cuvintele interzise
        return FORBIDDEN_WORDS.stream().noneMatch(input::contains);
    }
}