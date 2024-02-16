package inst.iop.LibraryManager.authentication.entities.constraints;

import inst.iop.LibraryManager.authentication.entities.validators.EmailConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = EmailConstraintValidator.class)
public @interface EmailConstraint {
  String message() default "The email address is either invalid or used";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  boolean isNotNullConstrain() default true;
}