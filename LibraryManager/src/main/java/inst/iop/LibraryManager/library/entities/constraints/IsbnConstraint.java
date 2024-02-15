package inst.iop.LibraryManager.library.entities.constraints;

import inst.iop.LibraryManager.library.entities.validators.IsbnConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = IsbnConstraintValidator.class)
public @interface IsbnConstraint {
  String message() default "Isbn must be in ISBN-10 or ISBN-13 format";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

  boolean isNotNullConstrain() default true;
}