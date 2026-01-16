package challengeme.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Bean Validation annotation used to flag fields that must be checked for
 * inappropriate language or offensive content.
 * Can be applied to fields (DTOs) or method parameters (Controllers).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContentValidator.class)
public @interface ValidContent {
    /** The error message returned when validation fails. */
    String message() default "Inappropriate content detected. Please keep the challenges respectful.";

    /** Allows the specification of validation groups. */
    Class<?>[] groups() default {};

    /** Can be used by clients to carry custom metadata objects. */
    Class<? extends Payload>[] payload() default {};
}