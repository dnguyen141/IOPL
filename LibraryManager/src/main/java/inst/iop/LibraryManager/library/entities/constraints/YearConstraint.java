package inst.iop.LibraryManager.library.entities.constraints;

import inst.iop.LibraryManager.library.entities.validators.YearConstraintValidator;
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
@Constraint(validatedBy = YearConstraintValidator.class)
public @interface YearConstraint {
  String message() default "Year must be equal or smaller than current year";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  boolean isNotNullConstrain() default true;
}
