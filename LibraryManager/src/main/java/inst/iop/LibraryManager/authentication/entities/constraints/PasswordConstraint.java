package inst.iop.LibraryManager.authentication.entities.constraints;

import inst.iop.LibraryManager.authentication.entities.validators.PasswordConstraintValidator;
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
@Constraint(validatedBy = PasswordConstraintValidator.class)
public @interface PasswordConstraint {
  String message() default "The password must contain at least one lowercase Latin character and " +
      "a length of at least and at most 20 characters";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

  boolean isNotNullConstrain() default true;
}