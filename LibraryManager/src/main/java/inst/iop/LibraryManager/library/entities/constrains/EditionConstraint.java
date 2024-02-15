package inst.iop.LibraryManager.library.entities.constrains;

import inst.iop.LibraryManager.library.entities.validators.EditionConstrainValidator;
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
@Constraint(validatedBy = EditionConstrainValidator.class)
public @interface EditionConstrain {
  String message() default "Edition must be equal or greater than 1 and equal or smaller than 100";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

  boolean isNotNullConstrain() default true;
}
