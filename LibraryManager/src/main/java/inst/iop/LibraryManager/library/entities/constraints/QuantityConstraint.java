package inst.iop.LibraryManager.library.entities.constraints;

import inst.iop.LibraryManager.library.entities.validators.QuantityConstraintValidator;
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
@Constraint(validatedBy = QuantityConstraintValidator.class)
public @interface QuantityConstraint {
  String message() default "Quantity must be equal or smaller than 1 and equal or smaller than 99";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  boolean isNotNullConstrain() default true;
}
