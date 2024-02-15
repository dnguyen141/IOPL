package inst.iop.LibraryManager.authentication.entities.constrains;

import inst.iop.LibraryManager.authentication.entities.validators.IntegerConstraintValidator;
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
@Constraint(validatedBy = IntegerConstraintValidator.class)
public @interface IntegerConstraint {
  String message() default "";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

  boolean isNotNullConstrain() default true;

  int min() default 1;

  int max() default 50;
}
