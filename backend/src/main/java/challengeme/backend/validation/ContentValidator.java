package challengeme.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.core.io.ClassPathResource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator implementation for the {@link ValidContent} annotation.
 * It loads a blacklist of words from a configuration file and checks
 * if any forbidden term is present in the provided input string.
 */
public class ContentValidator implements ConstraintValidator<ValidContent, String> {
    /** Thread-safe set containing the forbidden vocabulary. */
    private static final Set<String> FORBIDDEN_WORDS = new HashSet<>();

    static {
        // Static block to initialize the forbidden words list once during application startup.
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("bad-words.txt").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Trim whitespaces and remove invisible control characters to prevent evasion
                String word = line.trim().toLowerCase().replaceAll("[\\p{Cf}]", "");
                if (!word.isEmpty()) {
                    FORBIDDEN_WORDS.add(word);
                }
            }
        } catch (Exception e) {
            // Logs an error if the dictionary resource is missing or inaccessible
            System.err.println("Error loading bad-words.txt");
        }
    }

    /**
     * Checks if the provided text contains any forbidden words.
     * @param value The text content to validate.
     * @param context Context in which the constraint is evaluated.
     * @return false if at least one forbidden word is detected, true otherwise.
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null or blank values are considered valid by this specific validator;
        // @NotBlank should be used for presence validation.
        if (value == null || value.isBlank()) return true;

        String input = value.toLowerCase();

        // Perform a substring search against the forbidden words dictionary
        return FORBIDDEN_WORDS.stream().noneMatch(input::contains);
    }
}