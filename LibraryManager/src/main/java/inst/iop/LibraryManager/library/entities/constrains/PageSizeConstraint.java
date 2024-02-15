package inst.iop.LibraryManager.library.entities.constrains;

import inst.iop.LibraryManager.library.entities.validators.PageNumberConstrainValidator;
import inst.iop.LibraryManager.library.entities.validators.PageSizeConstrainValidator;
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
@Constraint(validatedBy = PageSizeConstrainValidator.class)
public @interface PageSizeConstrain {
  String message() default "Page size must be at least 1";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

  boolean isNotNullConstrain() default false;
}
