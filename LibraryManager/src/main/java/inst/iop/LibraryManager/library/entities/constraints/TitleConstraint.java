package inst.iop.LibraryManager.library.entities.constraints;

import inst.iop.LibraryManager.library.entities.validators.TitleConstraintValidator;
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
@Constraint(validatedBy = TitleConstraintValidator.class)
public @interface TitleConstraint {
  String message() default "Book title must have at least 1 character and at most 100 characters";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  boolean isNotNullConstrain() default true;
}