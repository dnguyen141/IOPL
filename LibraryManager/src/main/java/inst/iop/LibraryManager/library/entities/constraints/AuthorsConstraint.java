package inst.iop.LibraryManager.library.entities.constraints;

import inst.iop.LibraryManager.library.entities.validators.AuthorsConstraintValidator;
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
@Constraint(validatedBy = AuthorsConstraintValidator.class)
public @interface AuthorsConstraint {
  String message() default "Book authors should have at least 1 character, at most 100 character(s)" +
      "and separated by commas if there are more than one author";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  boolean isNotNullConstrain() default true;
}