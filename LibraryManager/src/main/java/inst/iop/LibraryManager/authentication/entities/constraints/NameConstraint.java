package inst.iop.LibraryManager.authentication.entities.constraints;

import inst.iop.LibraryManager.authentication.entities.validators.NameConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NameConstraintValidator.class)
public @interface NameConstraint {
  String message() default "Name must be at least 1 and at most 50 characters";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

  boolean isNotNullConstrain() default true;

  int min() default 1;

  int max() default 50;
}